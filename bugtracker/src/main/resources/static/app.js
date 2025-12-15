const API_URL = 'http://localhost:8080/api';

let currentUser = null;
let allBugs = [];
let allUsers = [];
let allProjects = [];
let dropdownsReady = false;
const requestCache = new Map();
const CACHE_DURATION = 5000;

async function cachedFetch(url) {
    const now = Date.now();
    const cached = requestCache.get(url);
    
    if (cached && (now - cached.timestamp) < CACHE_DURATION) {
        return cached.data;
    }
    
    const response = await fetch(url);
    const data = await response.json();
    requestCache.set(url, { data, timestamp: now });
    return data;
}

function showLoading(show = true) {
    const loader = document.getElementById('mainLoader');
    if (loader) {
        loader.style.display = show ? 'block' : 'none';
    }
}

function closeModal(modalId) {
    const modalElement = document.getElementById(modalId);
    const modal = bootstrap.Modal.getInstance(modalElement);
    if (modal) {
        modal.hide();
    }
    
    setTimeout(() => {
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());
        document.body.classList.remove('modal-open');
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';
    }, 100);
}

document.addEventListener("DOMContentLoaded", function() {
    const userString = localStorage.getItem('currentUser');
    if (!userString) {
        window.location.href = 'login.html';
        return;
    }
    currentUser = JSON.parse(userString);
    verileriYukle();
    

});

async function verileriYukle() {
    showLoading(true);
    try {
        const [bugsData, usersData, projectsData] = await Promise.all([
            cachedFetch(`${API_URL}/bugs`),
            cachedFetch(`${API_URL}/users`),
            cachedFetch(`${API_URL}/projects`)
        ]);

        allBugs = bugsData;
        allUsers = usersData;
        allProjects = projectsData;

        updateIstatistikler();

        renderBuglar();
        renderKullanicilar();
        renderProjeler();
        
        if (!dropdownsReady) {
            initDropdowns();
            dropdownsReady = true;
        }
    } catch (error) {
        console.error('Veri yükleme hatası:', error);
    } finally {
        showLoading(false);
    }
}

function initDropdowns() {
    const projectOptions = allProjects.map(p => 
        `<option value="${p.id}">${p.name} (${p.projectKey})</option>`
    ).join('');
    
    document.getElementById('bugProje').innerHTML = '<option value="">Proje Seçin</option>' + projectOptions;
    document.getElementById('editBugProje').innerHTML = '<option value="">Proje Yok</option>' + projectOptions;
    
    const developers = allUsers.filter(u => u.role === 'DEVELOPER' || u.role === 'Developer');
    const testers = allUsers.filter(u => u.role === 'TESTER' || u.role === 'Tester');
    
    const developerOptions = developers.map(d => 
        `<option value="${d.id}">${d.username} - ${d.fullName || ''}</option>`
    ).join('');
    
    const testerOptions = testers.map(t => 
        `<option value="${t.id}">${t.username} - ${getRolText(t.role)}</option>`
    ).join('');
    
    document.getElementById('bugDeveloper').innerHTML = '<option value="">Developer Seçin (İsteğe Bağlı)</option>' + developerOptions;
    document.getElementById('bugTester').innerHTML = '<option value="">Tester Seçin (İsteğe Bağlı)</option>' + testerOptions;
    document.getElementById('editBugDeveloper').innerHTML = '<option value="">Developer Seçin</option>' + developerOptions;
    document.getElementById('editBugTester').innerHTML = '<option value="">Tester Seçin</option>' + testerOptions;
    document.getElementById('assignDeveloper').innerHTML = '<option value="">Developer Seçin...</option>' + developerOptions;
    document.getElementById('assignTesterReporter').innerHTML = '<option value="">Tester Seçin...</option>' + testerOptions;
}

function updateIstatistikler() {
    document.getElementById('toplamBug').textContent = allBugs.length;
    document.getElementById('acikBug').textContent = allBugs.filter(b => b.status === 'OPEN').length;
    document.getElementById('devamEdenBug').textContent = allBugs.filter(b => b.status === 'IN_PROGRESS').length;
    document.getElementById('cozulmusBug').textContent = allBugs.filter(b => b.status === 'RESOLVED').length;
}



async function sadeceBuglariYukle() {
    try {
        requestCache.delete(`${API_URL}/bugs`);
        const response = await fetch(`${API_URL}/bugs`);
        allBugs = await response.json();
        updateIstatistikler();

        renderBuglar();
    } catch (error) {
        console.error('Bug yükleme hatası:', error);
    }
}

let searchDebounce = null;
function bugFiltrele() {
    clearTimeout(searchDebounce);
    searchDebounce = setTimeout(() => {
        const status = document.getElementById('filterStatus').value;
        const priority = document.getElementById('filterPriority').value;
        const search = document.getElementById('searchBug').value;
        const filtre = {};
        if (status) filtre.status = status;
        if (priority) filtre.priority = priority;
        if (search) filtre.search = search;
        renderBuglar(filtre);
    }, 300);
}

const ITEMS_PER_PAGE = 20;
let currentPage = 1;

function renderBuglar(filtre = null) {
    let buglar = [...allBugs];
    
    if (filtre) {
        if (filtre.status) buglar = buglar.filter(b => b.status === filtre.status);
        if (filtre.priority) buglar = buglar.filter(b => b.priority === filtre.priority);
        if (filtre.search) buglar = buglar.filter(b => b.title.toLowerCase().includes(filtre.search.toLowerCase()));
    }

    const liste = document.getElementById('bugListesi');
    if (!buglar || buglar.length === 0) {
        liste.innerHTML = '<p class="text-muted text-center p-3">Bug bulunamadı.</p>';
        return;
    }

    buglar.reverse();
    const totalPages = Math.ceil(buglar.length / ITEMS_PER_PAGE);
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const endIndex = startIndex + ITEMS_PER_PAGE;
    const visibleBugs = buglar.slice(startIndex, endIndex);
    const fragment = document.createDocumentFragment();
    const tempDiv = document.createElement('div');
    
    const htmlArray = visibleBugs.map(bug => createBugCard(bug));
    tempDiv.innerHTML = htmlArray.join('');
    
    while (tempDiv.firstChild) {
        fragment.appendChild(tempDiv.firstChild);
    }
    const paginationHTML = totalPages > 1 ? `
        <div class="d-flex justify-content-center mt-3">
            <button class="btn btn-sm btn-outline-primary me-2" ${currentPage === 1 ? 'disabled' : ''} onclick="changePage(${currentPage - 1})">
                <i class="ti ti-chevron-left"></i> Önceki
            </button>
            <span class="align-self-center mx-3">Sayfa ${currentPage} / ${totalPages}</span>
            <button class="btn btn-sm btn-outline-primary ms-2" ${currentPage === totalPages ? 'disabled' : ''} onclick="changePage(${currentPage + 1})">
                Sonraki <i class="ti ti-chevron-right"></i>
            </button>
        </div>
    ` : '';
    
    liste.innerHTML = '';
    liste.appendChild(fragment);
    liste.insertAdjacentHTML('beforeend', paginationHTML);
}

function changePage(page) {
    currentPage = page;
    renderBuglar();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function createBugCard(bug) {
    const status = bug.status || 'OPEN';
    const priority = bug.priority || 'LOW';
    const raporlayan = (bug.reporter && bug.reporter.username) ? bug.reporter.username : 'Anonim';
    const developer = (bug.assignee && bug.assignee.username) ? bug.assignee.username : null;
    const tester = (bug.tester && bug.tester.username) ? bug.tester.username : null;
    const proje = bug.project ? bug.project.name : null;
    const tarih = bug.createdAt ? new Date(bug.createdAt).toLocaleDateString('tr-TR') : '-';

    const durumRenk = getDurumRenk(status);
    const oncelikRenk = getOncelikRenk(priority);
    
    const isAdmin = currentUser.role === 'ADMIN' || currentUser.role === 'Admin';
    const isTester = currentUser.role === 'TESTER' || currentUser.role === 'Tester';
    const canCloseOrReopen = isAdmin || isTester;

    let atananlarBilgi = '';
    if (developer || tester) {
        if (developer) {
            atananlarBilgi += `<span class="text-primary small me-3"><i class="ti ti-code"></i> Developer: ${developer}</span>`;
        }
        if (tester) {
            atananlarBilgi += `<span class="text-success small me-3"><i class="ti ti-user-check"></i> Tester: ${tester}</span>`;
        }
    } else {
        atananlarBilgi = '<span class="badge bg-warning text-dark">Atanmamış</span>';
    }

    return `
        <div class="card mb-3">
            <div class="card-status-top bg-${durumRenk}"></div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <h3 class="card-title">
                            ${bug.title || 'Başlıksız'}
                            <span class="badge bg-${durumRenk} text-white ms-2" style="font-size: 0.7em">${getDurumText(status)}</span>
                        </h3>
                        <p class="text-muted">${bug.description || 'Açıklama yok'}</p>
                        <div class="mt-3">
                            <span class="badge bg-${oncelikRenk} text-white me-2">${getOncelikText(priority)}</span>
                            ${proje ? `<span class="badge bg-purple text-white me-2"><i class="ti ti-folder"></i> ${proje}</span>` : ''}
                            <span class="text-muted small me-3"><i class="ti ti-user"></i> ${raporlayan}</span>
                            ${atananlarBilgi}
                            <span class="text-muted small"><i class="ti ti-calendar"></i> ${tarih}</span>
                        </div>
                    </div>
                    <div class="col-md-4 text-end">
                        <div class="btn-group-vertical btn-group-sm" role="group">
                            ${(status === 'OPEN' || status === 'REOPENED') && !developer && !tester ? `
                                <button class="btn btn-primary" onclick="bugAtaGoster(${bug.id})">
                                    <i class="ti ti-user-plus"></i> Kullanıcıya Ata
                                </button>
                            ` : ''}
                            ${status === 'IN_PROGRESS' ? `
                                <button class="btn btn-success" onclick="bugCoz(${bug.id})">
                                    <i class="ti ti-check"></i> Çözüldü
                                </button>
                            ` : ''}
                            ${status === 'RESOLVED' && canCloseOrReopen ? `
                                <button class="btn btn-info" onclick="bugKapat(${bug.id})">
                                    <i class="ti ti-lock"></i> Kapat
                                </button>
                                <button class="btn btn-warning" onclick="bugYenidenAc(${bug.id})">
                                    <i class="ti ti-refresh"></i> Yeniden Aç
                                </button>
                            ` : ''}
                            ${status === 'CLOSED' && canCloseOrReopen ? `
                                <button class="btn btn-warning" onclick="bugYenidenAc(${bug.id})">
                                    <i class="ti ti-refresh"></i> Yeniden Aç
                                </button>
                            ` : ''}
                            <button class="btn btn-secondary" onclick="bugDuzenle(${bug.id})">
                                <i class="ti ti-edit"></i> Düzenle
                            </button>
                            ${isAdmin ? `
                                <button class="btn btn-danger" onclick="bugSil(${bug.id})">
                                    <i class="ti ti-trash"></i> Sil
                                </button>
                            ` : ''}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function renderKullanicilar() {
    const tbody = document.getElementById('kullaniciListesi');
    if (!allUsers || allUsers.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Kullanıcı yok.</td></tr>';
        return;
    }
    
    const htmlArray = allUsers.map(user => {
        const durumRenk = user.active ? 'success' : 'danger';
        const durumYazi = user.active ? 'Aktif' : 'Pasif';
        return `
            <tr>
                <td>${user.id}</td>
                <td><span class="fw-bold">${user.username}</span><div class="text-muted small">${user.fullName || ''}</div></td>
                <td>${user.email}</td>
                <td><span class="badge bg-primary text-white">${getRolText(user.role)}</span></td>
                <td><span class="badge bg-${durumRenk} text-white">${durumYazi}</span></td>
                <td>
                    ${currentUser.role === 'Admin' || currentUser.role === 'ADMIN' ? `
                        <button class="btn btn-sm btn-danger" onclick="kullaniciSil(${user.id})"><i class="ti ti-trash"></i></button>
                    ` : ''}
                </td>
            </tr>
        `;
    });
    
    tbody.innerHTML = htmlArray.join('');
}

function renderProjeler() {
    const tbody = document.getElementById('projelerListesi');
    if (!allProjects || allProjects.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Proje yok.</td></tr>';
        return;
    }
    
    const htmlArray = allProjects.map(proje => `
        <tr>
            <td>${proje.id}</td>
            <td><strong>${proje.name}</strong><div class="text-muted small">${proje.projectKey}</div></td>
            <td>${proje.description || '-'}</td>
        </tr>
    `);
    
    tbody.innerHTML = htmlArray.join('');
}

function yeniBugModalAc() {
    document.getElementById('bugBaslik').value = '';
    document.getElementById('bugAciklama').value = '';
    document.getElementById('bugProje').value = '';
    document.getElementById('bugDeveloper').value = '';
    document.getElementById('bugTester').value = '';
    document.getElementById('bugOncelik').value = 'MEDIUM';
    document.getElementById('bugOnem').value = 'MAJOR';
}

async function bugKaydet() {
    const baslik = document.getElementById('bugBaslik').value;
    const aciklama = document.getElementById('bugAciklama').value;
    const oncelik = document.getElementById('bugOncelik').value;
    const onem = document.getElementById('bugOnem').value;
    const projeId = document.getElementById('bugProje').value;
    const developerId = document.getElementById('bugDeveloper').value;
    const testerId = document.getElementById('bugTester').value;

    if (!baslik) {
        alert('Başlık zorunludur!');
        return;
    }

    if (!projeId) {
        alert('Proje seçimi zorunludur!');
        return;
    }

    const bug = {
        title: baslik,
        description: aciklama,
        priority: oncelik,
        severity: onem,
        project: { id: parseInt(projeId) }
    };

    try {
        const response = await fetch(`${API_URL}/bugs?reporterId=${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bug)
        });

        if (response.ok) {
            const createdBug = await response.json();

            if (developerId) {
                await fetch(`${API_URL}/bugs/${createdBug.id}/assign?developerId=${developerId}&assignerId=${currentUser.id}`, {
                    method: 'PUT'
                });
            }

            if (testerId) {
                await fetch(`${API_URL}/bugs/${createdBug.id}/assign-tester?testerId=${testerId}&assignerId=${currentUser.id}`, {
                    method: 'PUT'
                });
            }

            closeModal('modal-bug');
            await sadeceBuglariYukle();
            alert('Bug başarıyla oluşturuldu!');
        }
    } catch (error) {
        console.error(error);
        alert('Hata oluştu!');
    }
}

async function bugDuzenle(bugId) {
    const bug = allBugs.find(b => b.id === bugId);
    if (!bug) return;

    document.getElementById('editBugId').value = bug.id;
    document.getElementById('editBugBaslik').value = bug.title;
    document.getElementById('editBugAciklama').value = bug.description || '';
    document.getElementById('editBugOncelik').value = bug.priority;
    document.getElementById('editBugOnem').value = bug.severity;
    document.getElementById('editBugProje').value = bug.project ? bug.project.id : '';
    document.getElementById('editBugDeveloper').value = bug.assignee ? bug.assignee.id : '';
    document.getElementById('editBugTester').value = bug.tester ? bug.tester.id : '';

    new bootstrap.Modal(document.getElementById('modal-edit-bug')).show();
}

async function bugGuncelle() {
    const bugId = document.getElementById('editBugId').value;
    const projeId = document.getElementById('editBugProje').value;
    const developerId = document.getElementById('editBugDeveloper').value;
    const testerId = document.getElementById('editBugTester').value;
    
    const bug = {
        title: document.getElementById('editBugBaslik').value,
        description: document.getElementById('editBugAciklama').value,
        priority: document.getElementById('editBugOncelik').value,
        severity: document.getElementById('editBugOnem').value
    };
    if (projeId) bug.project = { id: parseInt(projeId) };

    try {
        const response = await fetch(`${API_URL}/bugs/${bugId}?updaterId=${currentUser.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bug)
        });

        if (response.ok) {
            if (developerId) {
                await fetch(`${API_URL}/bugs/${bugId}/assign?developerId=${developerId}&assignerId=${currentUser.id}`, { method: 'PUT' });
            }
            if (testerId) {
                await fetch(`${API_URL}/bugs/${bugId}/assign-tester?testerId=${testerId}&assignerId=${currentUser.id}`, { method: 'PUT' });
            }
            closeModal('modal-edit-bug');
            await sadeceBuglariYukle();
            alert('Bug güncellendi!');
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function bugSil(bugId) {
    if (!confirm('Bu bug\'ı silmek istediğinize emin misiniz?')) return;
    try {
        const response = await fetch(`${API_URL}/bugs/${bugId}?userId=${currentUser.id}`, { method: 'DELETE' });
        if (response.ok) {
            await sadeceBuglariYukle();
            alert('Bug silindi!');
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function bugAtaGoster(bugId) {
    document.getElementById('assignBugId').value = bugId;
    document.getElementById('assignDeveloper').value = '';
    document.getElementById('assignTesterReporter').value = '';
    new bootstrap.Modal(document.getElementById('modal-assign-bug')).show();
}

async function bugAtaKaydet() {
    const bugId = document.getElementById('assignBugId').value;
    const devId = document.getElementById('assignDeveloper').value;
    const testerId = document.getElementById('assignTesterReporter').value;

    if (!devId && !testerId) {
        alert('Lütfen en az bir kullanıcı seçin (Developer veya Tester)!');
        return;
    }

    try {
        if (devId) {
            const response = await fetch(`${API_URL}/bugs/${bugId}/assign?developerId=${devId}&assignerId=${currentUser.id}`, { method: 'PUT' });
            if (!response.ok) {
                alert('Developer atama hatası: ' + await response.text());
                return;
            }
        }
        if (testerId) {
            const response = await fetch(`${API_URL}/bugs/${bugId}/assign-tester?testerId=${testerId}&assignerId=${currentUser.id}`, { method: 'PUT' });
            if (!response.ok) {
                alert('Tester atama hatası: ' + await response.text());
                return;
            }
        }
        closeModal('modal-assign-bug');
        await sadeceBuglariYukle();
        alert('Bug atandı!');
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function bugCoz(bugId) {
    if (!confirm('Bu bug\'ı çözüldü olarak işaretlemek istiyor musunuz?')) return;
    try {
        const response = await fetch(`${API_URL}/bugs/${bugId}/resolve?resolverId=${currentUser.id}`, { method: 'PUT' });
        if (response.ok) {
            await sadeceBuglariYukle();
            alert('Bug çözüldü olarak işaretlendi!');
        } else {
            alert('Hata: ' + await response.text());
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function bugKapat(bugId) {
    if (!confirm('Bu bug\'ı kapatmak istiyor musunuz?')) return;
    try {
        const response = await fetch(`${API_URL}/bugs/${bugId}/close?closerId=${currentUser.id}`, { method: 'PUT' });
        if (response.ok) {
            await sadeceBuglariYukle();
            alert('Bug kapatıldı!');
        } else {
            alert('Hata: ' + await response.text());
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function bugYenidenAc(bugId) {
    if (!confirm('Bu bug\'ı yeniden açmak istiyor musunuz?')) return;
    try {
        const response = await fetch(`${API_URL}/bugs/${bugId}/reopen?reopenerId=${currentUser.id}`, { method: 'PUT' });
        if (response.ok) {
            await sadeceBuglariYukle();
            alert('Bug yeniden açıldı!');
        } else {
            alert('Hata: ' + await response.text());
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

function filtreTemizle() {
    document.getElementById('filterStatus').value = '';
    document.getElementById('filterPriority').value = '';
    document.getElementById('searchBug').value = '';
    currentPage = 1;
    renderBuglar();
}

async function kullaniciKaydet() {
    const username = document.getElementById('userUsername').value;
    const email = document.getElementById('userEmail').value;
    const password = document.getElementById('userPassword').value;
    const fullName = document.getElementById('userFullName').value;
    const role = document.getElementById('userRole').value;
    if (!username || !email || !password) {
        alert('Zorunlu alanları doldurun!');
        return;
    }
    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName, role })
        });
        if (response.ok) {
            closeModal('modal-user');
            document.getElementById('userUsername').value = '';
            document.getElementById('userEmail').value = '';
            document.getElementById('userPassword').value = '';
            document.getElementById('userFullName').value = '';
            
            requestCache.delete(`${API_URL}/users`);
            allUsers = await fetch(`${API_URL}/users`).then(r => r.json());
            renderKullanicilar();
            initDropdowns();
            alert('Kullanıcı oluşturuldu!');
        } else {
            alert('Hata: ' + await response.text());
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function kullaniciSil(userId) {
    if (!confirm('Bu kullanıcıyı silmek istediğinize emin misiniz?')) return;
    try {
        const response = await fetch(`${API_URL}/users/${userId}`, { method: 'DELETE' });
        if (response.ok) {
            requestCache.delete(`${API_URL}/users`);
            allUsers = await fetch(`${API_URL}/users`).then(r => r.json());
            renderKullanicilar();
            initDropdowns();
            alert('Kullanıcı silindi!');
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}

async function projeKaydet() {
    const name = document.getElementById('projectName').value;
    const key = document.getElementById('projectKey').value;
    const description = document.getElementById('projectDescription').value;
    if (!name || !key) {
        alert('Proje adı ve anahtarı zorunludur!');
        return;
    }
    const proje = { name, projectKey: key, description };
    try {
        const response = await fetch(`${API_URL}/projects?ownerId=${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(proje)
        });
        if (response.ok) {
            closeModal('modal-project');
            document.getElementById('projectName').value = '';
            document.getElementById('projectKey').value = '';
            document.getElementById('projectDescription').value = '';
            
            requestCache.delete(`${API_URL}/projects`);
            allProjects = await fetch(`${API_URL}/projects`).then(r => r.json());
            renderProjeler();
            initDropdowns();
            alert('Proje oluşturuldu!');
        } else {
            alert('Hata: ' + await response.text());
        }
    } catch (error) {
        alert('Hata: ' + error.message);
    }
}



function cikisYap() {
    if (confirm('Çıkış yapmak istiyor musunuz?')) {
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
    }
}

function getDurumText(status) {
    const map = {
        'OPEN': 'Açık',
        'IN_PROGRESS': 'Devam Ediyor',
        'RESOLVED': 'Çözüldü',
        'REOPENED': 'Yeniden Açıldı',
        'CLOSED': 'Kapatıldı'
    };
    return map[status?.toUpperCase()] || status;
}

function getDurumRenk(status) {
    const map = {
        'OPEN': 'primary',
        'IN_PROGRESS': 'yellow',
        'RESOLVED': 'success',
        'REOPENED': 'orange',
        'CLOSED': 'purple'
    };
    return map[status] || 'azure';
}

function getOncelikText(priority) {
    const map = {
        'LOW': 'Düşük',
        'MEDIUM': 'Orta',
        'HIGH': 'Yüksek',
        'CRITICAL': 'Kritik'
    };
    return map[priority?.toUpperCase()] || priority;
}

function getOncelikRenk(priority) {
    const map = {
        'LOW': 'info',
        'MEDIUM': 'warning',
        'HIGH': 'orange',
        'CRITICAL': 'danger'
    };
    return map[priority?.toUpperCase()] || 'secondary';
}

function getRolText(role) {
    const map = {
        'ADMIN': 'Yönetici',
        'Admin': 'Yönetici',
        'DEVELOPER': 'Geliştirici',
        'Developer': 'Geliştirici',
        'TESTER': 'Test Uzmanı / Raporlayıcı',
        'Tester': 'Test Uzmanı / Raporlayıcı'
    };
    return map[role] || role;
}
