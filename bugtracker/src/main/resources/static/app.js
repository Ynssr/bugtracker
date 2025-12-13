const API_URL = 'http://localhost:8080/api';

// Sayfa yüklendiğinde çalışır (Güvenli Başlatma)
document.addEventListener("DOMContentLoaded", function() {
    const user = localStorage.getItem('currentUser');
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    // Verileri Yükle
    verileriYukle();
});

// Tüm verileri sırasıyla yükle
async function verileriYukle() {
    await istatistikleriYukle();
    await buglariYukle();
    await kullanicilariYukle();
}

// 1. İstatistikleri Yükle
async function istatistikleriYukle() {
    try {
        const response = await fetch(`${API_URL}/bugs`);
        if (!response.ok) return;

        const buglar = await response.json();

        document.getElementById('toplamBug').textContent = buglar.length;
        document.getElementById('acikBug').textContent = buglar.filter(b => b.status === 'OPEN').length;
        document.getElementById('devamEdenBug').textContent = buglar.filter(b => b.status === 'IN_PROGRESS').length;
        document.getElementById('cozulmusBug').textContent = buglar.filter(b => b.status === 'RESOLVED').length;
    } catch (error) {
        console.error('İstatistik hatası:', error);
    }
}

// 2. Bug Listesini Yükle (Tam Korumalı Versiyon)
async function buglariYukle() {
    try {
        const response = await fetch(`${API_URL}/bugs`);
        if (!response.ok) throw new Error("Veri alınamadı");

        const buglar = await response.json();
        const liste = document.getElementById('bugListesi');

        if (!buglar || buglar.length === 0) {
            liste.innerHTML = '<p class="text-muted text-center p-3">Henüz kayıtlı bir bug bulunmuyor.</p>';
            return;
        }

        let html = '';
        // Bugları ters çevir (En yeni en üstte)
        buglar.reverse().forEach(bug => {
            // Hata koruması: Değerler null gelirse varsayılan ata
            const status = bug.status || 'CLOSED';
            const priority = bug.priority || 'LOW';
            const raporlayan = (bug.reporter && bug.reporter.username) ? bug.reporter.username : 'Anonim';
            const tarih = bug.createdAt ? new Date(bug.createdAt).toLocaleDateString('tr-TR') : '-';

            const durumRenk = getDurumRenk(status);
            const oncelikRenk = getOncelikRenk(priority);

            html += `
                <div class="card mb-3">
                    <div class="card-status-top bg-${durumRenk}"></div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col">
                                <h3 class="card-title">
                                    ${bug.title || 'Başlıksız'}
                                    <span class="badge bg-${durumRenk} text-white ms-2" style="font-size: 0.7em">
                                        ${getDurumText(status)}
                                    </span>
                                </h3>
                                <p class="text-muted">${bug.description || 'Açıklama yok'}</p>
                                <div class="d-flex justify-content-between align-items-center mt-3">
                                    <div>
                                        <span class="badge bg-${oncelikRenk} text-white">
                                            ${getOncelikText(priority)}
                                        </span>
                                        <span class="text-muted ms-2 small">
                                            <i class="ti ti-user"></i> ${raporlayan}
                                        </span>
                                    </div>
                                    <small class="text-muted">${tarih}</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });

        liste.innerHTML = html;
    } catch (error) {
        console.error('Bug listeleme hatası:', error);
        document.getElementById('bugListesi').innerHTML = `<div class="alert alert-danger">Veriler yüklenirken hata oluştu: ${error.message}</div>`;
    }
}

// 3. Kullanıcıları Yükle (Renkli Versiyon)
async function kullanicilariYukle() {
    try {
        const response = await fetch(`${API_URL}/users`);
        const kullanicilar = await response.json();
        const tbody = document.getElementById('kullaniciListesi');

        if (!kullanicilar || kullanicilar.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Kullanıcı yok.</td></tr>';
            return;
        }

        let html = '';
        kullanicilar.forEach(user => {
            const durumRenk = user.active ? 'success' : 'danger';
            const durumYazi = user.active ? 'Aktif' : 'Pasif';

            html += `
                <tr>
                    <td>${user.id}</td>
                    <td>
                        <span class="fw-bold">${user.username}</span>
                        <div class="text-muted small">${user.fullName || ''}</div>
                    </td>
                    <td>${user.email}</td>
                    <td><span class="badge bg-primary text-white">${getRolText(user.role)}</span></td>
                    <td><span class="badge bg-${durumRenk} text-white">${durumYazi}</span></td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    } catch (error) {
        console.error('Kullanıcı listeleme hatası:', error);
    }
}

// Bug Kaydetme Fonksiyonu
async function bugKaydet() {
    const baslik = document.getElementById('bugBaslik').value;
    const aciklama = document.getElementById('bugAciklama').value;
    const oncelik = document.getElementById('bugOncelik').value;
    const onem = document.getElementById('bugOnem').value;

    if (!baslik) { alert('Başlık zorunludur!'); return; }

    const userString = localStorage.getItem('currentUser');
    if (!userString) return;
    const currentUser = JSON.parse(userString);

    const bug = {
        title: baslik,
        description: aciklama,
        priority: oncelik,
        severity: onem
    };

    try {
        const response = await fetch(`${API_URL}/bugs?reporterId=${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bug)
        });

        if (response.ok) {
            alert('Bug başarıyla oluşturuldu!');
            document.getElementById('bugBaslik').value = '';
            document.getElementById('bugAciklama').value = '';

            // Modalı kapat
            const modalEl = document.getElementById('modal-bug');
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();

            verileriYukle();
        } else {
            alert('Hata oluştu.');
        }
    } catch (error) {
        console.error(error);
        alert('Sunucu hatası.');
    }
}

// Kullanıcı Kaydetme Fonksiyonu (Güvenli)
async function kullaniciKaydet() {
    const userString = localStorage.getItem('currentUser');
    if (!userString) return;
    const currentUser = JSON.parse(userString);

    if (currentUser.role !== 'Admin' && currentUser.role !== 'ADMIN') {
        alert("Sadece Yöneticiler kullanıcı ekleyebilir!");
        return;
    }

    const username = document.getElementById('userUsername').value;
    const email = document.getElementById('userEmail').value;
    const password = document.getElementById('userPassword').value;
    const fullName = document.getElementById('userFullName').value;
    const role = document.getElementById('userRole').value;

    if (!username || !password) { alert('Eksik bilgi girdiniz.'); return; }

    const saveButton = document.querySelector('#modal-user .btn-primary');
    saveButton.disabled = true;
    saveButton.innerHTML = 'Kaydediliyor...';

    try {
        const response = await fetch(`${API_URL}/users?requesterId=${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName, role })
        });

        if (response.ok) {
            alert('Kullanıcı oluşturuldu!');
            document.getElementById('userUsername').value = '';
            document.getElementById('userEmail').value = '';
            document.getElementById('userPassword').value = '';
            document.getElementById('userFullName').value = '';

            const modal = bootstrap.Modal.getInstance(document.getElementById('modal-user'));
            modal.hide();

            verileriYukle();
        } else {
            const err = await response.text();
            alert('Hata: ' + err);
        }
    } catch (error) {
        alert('Bağlantı hatası.');
    } finally {
        saveButton.disabled = false;
        saveButton.innerHTML = 'Kaydet';
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
    return map[status] || status;
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
    return map[priority] || map[priority?.toUpperCase()] || priority;
}

function getOncelikRenk(priority) {
    const map = {
        'LOW': 'info',
        'MEDIUM': 'warning',
        'HIGH': 'orange',
        'CRITICAL': 'danger'
    };
    return map[priority] || map[priority?.toUpperCase()] || 'secondary';
}

function getRolText(role) {
    const map = {
        'ADMIN': 'Yönetici',
        'Admin': 'Yönetici',
        'DEVELOPER': 'Geliştirici',
        'Developer': 'Geliştirici',
        'TESTER': 'Test Uzmanı',
        'Tester': 'Test Uzmanı',
        'REPORTER': 'Raporlayıcı'
    };
    return map[role] || role;
}