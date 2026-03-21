(function () {
    function setupStudentNavigation() {
        var body = document.body;
        var toggle = document.getElementById("studentNavToggle");
        var overlay = document.getElementById("studentNavOverlay");
        var sidebar = document.getElementById("studentSidebar");

        if (!body || !toggle || !overlay || !sidebar) {
            return;
        }

        function isDesktop() {
            return window.innerWidth > 1200;
        }

        function closeSidebar() {
            body.classList.remove("student-nav-open");
            toggle.setAttribute("aria-expanded", "false");
        }

        function syncToggleLabel() {
            var isOpen = body.classList.contains("student-nav-open");
            var label = isOpen ? "\u0110\u00f3ng menu sinh vi\u00ean" : "M\u1edf menu sinh vi\u00ean";

            toggle.setAttribute("aria-label", label);
            toggle.setAttribute("title", label);
            toggle.setAttribute("aria-expanded", isOpen ? "true" : "false");
        }

        toggle.addEventListener("click", function () {
            body.classList.toggle("student-nav-open");
            syncToggleLabel();
        });

        overlay.addEventListener("click", function () {
            closeSidebar();
            syncToggleLabel();
        });

        window.addEventListener("resize", function () {
            if (isDesktop()) {
                closeSidebar();
            }
            syncToggleLabel();
        });

        closeSidebar();
        syncToggleLabel();
    }

    function setupSelectAll() {
        var masterCheckboxes = document.querySelectorAll("[data-select-all]");

        if (!masterCheckboxes.length) {
            return;
        }

        masterCheckboxes.forEach(function (masterCheckbox) {
            var targetName = masterCheckbox.getAttribute("data-select-all");

            if (!targetName) {
                return;
            }

            masterCheckbox.addEventListener("change", function () {
                document.querySelectorAll("[data-select-item=\"" + targetName + "\"]").forEach(function (itemCheckbox) {
                    itemCheckbox.checked = masterCheckbox.checked;
                });
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupStudentNavigation();
        setupSelectAll();
    });
}());
