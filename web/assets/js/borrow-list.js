(function () {
    var root = document.getElementById('borrowAdminPage');
    if (!root) {
        return;
    }

    var contextPath = root.getAttribute('data-context-path') || '';
    var tabs = Array.prototype.slice.call(root.querySelectorAll('.borrow-tab'));
    var panes = Array.prototype.slice.call(root.querySelectorAll('.tab-pane'));
    var pendingBadge = document.getElementById('pendingTabBadge');
    var reloadScheduled = false;

    function setActiveTab(tabId) {
        tabs.forEach(function (tab) {
            var isActive = tab.getAttribute('data-tab') === tabId;
            tab.classList.toggle('active', isActive);
        });

        panes.forEach(function (pane) {
            pane.classList.toggle('active', pane.id === tabId);
        });
    }

    tabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            var targetTab = tab.getAttribute('data-tab');
            if (targetTab) {
                setActiveTab(targetTab);
            }
        });
    });

    function updatePendingBadge(count) {
        if (!pendingBadge) {
            return;
        }

        var safeCount = Number(count) || 0;
        pendingBadge.textContent = safeCount;
        pendingBadge.classList.toggle('is-hidden', safeCount <= 0);
    }

    function refreshPendingCount() {
        return fetch(contextPath + '/api/pending-count', {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
                .then(function (response) {
                    return response.ok ? response.json() : null;
                })
                .then(function (data) {
                    if (!data || typeof data.pendingCount === 'undefined') {
                        return;
                    }
                    updatePendingBadge(data.pendingCount);
                })
                .catch(function () {
                });
    }

    var initialHash = window.location.hash ? window.location.hash.replace('#', '') : '';
    if (initialHash && root.querySelector('.borrow-tab[data-tab="' + initialHash + '"]')) {
        setActiveTab(initialHash);
    }

    refreshPendingCount();
    window.setInterval(refreshPendingCount, 15000);

    document.addEventListener('adminNotification', function (event) {
        var data = event.detail || {};
        if (data.type === 'NEW_BORROW' || data.type === 'NEW_HOLD') {
            refreshPendingCount();
            if (!reloadScheduled) {
                reloadScheduled = true;
                window.setTimeout(function () {
                    window.location.reload();
                }, 2500);
            }
        }
    });
})();
