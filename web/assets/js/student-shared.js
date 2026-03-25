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

    function setupStudentCartMenu() {
        var menus = document.querySelectorAll(".student-cart-menu");
        if (!menus.length) {
            return;
        }

        document.addEventListener("click", function (event) {
            menus.forEach(function (menu) {
                if (!menu.contains(event.target)) {
                    menu.removeAttribute("open");
                }
            });
        });

        document.addEventListener("keydown", function (event) {
            if (event.key !== "Escape") {
                return;
            }

            menus.forEach(function (menu) {
                menu.removeAttribute("open");
            });
        });
    }

    function setupRealtimeBuyList() {
        var panel = document.querySelector("[data-buy-list-panel]");
        if (!panel) {
            return;
        }

        var rows = Array.prototype.slice.call(panel.querySelectorAll("[data-buy-list-row]"));
        if (!rows.length) {
            return;
        }

        var readyCountTargets = Array.prototype.slice.call(document.querySelectorAll("[data-buy-ready-count]"));
        var blockedCountTargets = Array.prototype.slice.call(panel.querySelectorAll("[data-buy-blocked-count]"));
        var totalTargets = Array.prototype.slice.call(panel.querySelectorAll("[data-buy-list-total]"));
        var blockedChip = panel.querySelector("[data-buy-blocked-chip]");
        var blockedAlert = panel.querySelector("[data-buy-blocked-alert]");
        var labels = {
            defaultButton: "C\u1eadp nh\u1eadt",
            invalidButton: "C\u1ea7n ki\u1ec3m tra",
            savingButton: "\u0110ang l\u01b0u...",
            savedButton: "\u0110\u00e3 l\u01b0u",
            errorButton: "L\u1ed7i l\u01b0u",
            readyStatus: "S\u1eb5n g\u1eedi duy\u1ec7t",
            blockedStatus: "C\u1ea7n ki\u1ec3m tra"
        };

        function parseNumber(value, fallback) {
            var parsed = Number(value);
            return Number.isFinite(parsed) ? parsed : fallback;
        }

        function normalizeQuantity(value) {
            var parsed = parseInt(value, 10);
            if (!Number.isFinite(parsed) || parsed < 1) {
                return 1;
            }
            return parsed;
        }

        function formatAmount(value) {
            if (!Number.isFinite(value)) {
                return "0";
            }

            var rounded = Math.round(value * 100) / 100;
            return Number.isInteger(rounded) ? rounded.toFixed(1) : String(rounded);
        }

        function setVisible(element, visible) {
            if (!element) {
                return;
            }
            element.style.display = visible ? "" : "none";
        }

        function setButtonState(button, label, disabled, resetDelay) {
            if (!button) {
                return;
            }

            button.textContent = label;
            button.disabled = !!disabled;

            if (button._buyListResetTimer) {
                window.clearTimeout(button._buyListResetTimer);
                button._buyListResetTimer = null;
            }

            if (resetDelay) {
                button._buyListResetTimer = window.setTimeout(function () {
                    button.textContent = labels.defaultButton;
                    button.disabled = false;
                }, resetDelay);
            }
        }

        function buildStatusMessage(available, unitPrice, quantity) {
            var reasons = [];

            if (available <= 0) {
                reasons.push("s\u00e1ch \u0111ang h\u1ebft h\u00e0ng");
            } else if (quantity > available) {
                reasons.push("s\u1ed1 l\u01b0\u1ee3ng \u0111ang v\u01b0\u1ee3t t\u1ed3n kho");
            }

            if (unitPrice <= 0) {
                reasons.push("s\u00e1ch ch\u01b0a c\u00f3 gi\u00e1");
            }

            if (!reasons.length) {
                return "";
            }

            return "Kh\u00f4ng th\u1ec3 g\u1eedi v\u00ec " + reasons.join(" v\u00e0 ") + ".";
        }

        function refreshRow(row) {
            var input = row.querySelector("[data-buy-qty-input]");
            var lineTotalElement = row.querySelector("[data-buy-line-total]");
            var statusElement = row.querySelector("[data-buy-status-label]");
            var noteElement = row.querySelector("[data-buy-status-note]");
            var orderButton = row.querySelector("[data-buy-order-button]");
            var saveButton = row.querySelector("[data-buy-save-button]");
            var quantity = normalizeQuantity(input ? input.value : 1);
            var available = parseNumber(row.getAttribute("data-available"), 0);
            var unitPrice = parseNumber(row.getAttribute("data-unit-price"), 0);
            var lineTotal = unitPrice * quantity;
            var canOrder = unitPrice > 0 && available >= quantity;

            if (input) {
                input.value = quantity;
            }

            row.dataset.quantity = String(quantity);
            row.dataset.canOrder = canOrder ? "true" : "false";
            row.dataset.lineTotal = String(lineTotal);

            if (lineTotalElement) {
                lineTotalElement.textContent = formatAmount(lineTotal);
            }

            if (statusElement) {
                statusElement.textContent = canOrder ? labels.readyStatus : labels.blockedStatus;
                statusElement.classList.toggle("status-ok", canOrder);
                statusElement.classList.toggle("status-bad", !canOrder);
            }

            if (noteElement) {
                noteElement.textContent = buildStatusMessage(available, unitPrice, quantity);
                setVisible(noteElement, !canOrder);
            }

            if (orderButton) {
                orderButton.disabled = !canOrder;
            }

            if (saveButton && !saveButton.dataset.busy) {
                if (canOrder) {
                    setButtonState(saveButton, labels.defaultButton, false);
                } else {
                    setButtonState(saveButton, labels.invalidButton, true);
                }
            }

            return {
                canOrder: canOrder,
                lineTotal: lineTotal
            };
        }

        function refreshSummary() {
            var readyCount = 0;
            var blockedCount = 0;
            var totalAmount = 0;

            rows.forEach(function (row) {
                var snapshot = refreshRow(row);
                totalAmount += snapshot.lineTotal;

                if (snapshot.canOrder) {
                    readyCount++;
                } else {
                    blockedCount++;
                }
            });

            readyCountTargets.forEach(function (target) {
                target.textContent = readyCount;
            });

            blockedCountTargets.forEach(function (target) {
                target.textContent = blockedCount;
            });

            totalTargets.forEach(function (target) {
                target.textContent = formatAmount(totalAmount);
            });

            setVisible(blockedChip, blockedCount > 0);
            setVisible(blockedAlert, blockedCount > 0);
        }

        function clearPendingSync(row) {
            if (row._buyListSyncTimer) {
                window.clearTimeout(row._buyListSyncTimer);
                row._buyListSyncTimer = null;
            }
        }

        function syncRow(row, immediate) {
            var form = row.querySelector("[data-buy-qty-form]");
            var input = row.querySelector("[data-buy-qty-input]");
            var saveButton = row.querySelector("[data-buy-save-button]");
            var quantity = normalizeQuantity(input ? input.value : 1);
            var available = parseNumber(row.getAttribute("data-available"), 0);
            var unitPrice = parseNumber(row.getAttribute("data-unit-price"), 0);

            clearPendingSync(row);

            if (!form || unitPrice <= 0 || available < quantity) {
                if (saveButton) {
                    setButtonState(saveButton, labels.invalidButton, true);
                }
                return;
            }

            if (row.dataset.lastSyncedQty === String(quantity)) {
                return;
            }

            function sendUpdate() {
                var payload = new URLSearchParams(new FormData(form));
                var controller = typeof AbortController === "function" ? new AbortController() : null;

                if (row._buyListController) {
                    row._buyListController.abort();
                }
                row._buyListController = controller;

                payload.set("quantity", String(quantity));

                if (saveButton) {
                    saveButton.dataset.busy = "true";
                    setButtonState(saveButton, labels.savingButton, true);
                }

                fetch(form.action, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                        "X-Requested-With": "XMLHttpRequest"
                    },
                    body: payload.toString(),
                    signal: controller ? controller.signal : undefined
                })
                        .then(function (response) {
                            if (!response.ok) {
                                throw new Error("sync_failed");
                            }

                            row.dataset.lastSyncedQty = String(quantity);
                            if (saveButton) {
                                setButtonState(saveButton, labels.savedButton, false, 1200);
                            }
                        })
                        .catch(function (error) {
                            if (error && error.name === "AbortError") {
                                return;
                            }

                            if (saveButton) {
                                setButtonState(saveButton, labels.errorButton, false, 1600);
                            }
                        })
                        .finally(function () {
                            if (saveButton) {
                                delete saveButton.dataset.busy;
                            }
                        });
            }

            if (immediate) {
                sendUpdate();
            } else {
                row._buyListSyncTimer = window.setTimeout(sendUpdate, 450);
            }
        }

        rows.forEach(function (row) {
            var form = row.querySelector("[data-buy-qty-form]");
            var input = row.querySelector("[data-buy-qty-input]");

            refreshRow(row);
            row.dataset.lastSyncedQty = row.dataset.quantity || "1";

            if (!input || !form) {
                return;
            }

            input.addEventListener("input", function () {
                refreshRow(row);
                refreshSummary();
                syncRow(row, false);
            });

            input.addEventListener("change", function () {
                refreshRow(row);
                refreshSummary();
                syncRow(row, true);
            });

            form.addEventListener("submit", function (event) {
                event.preventDefault();
                refreshRow(row);
                refreshSummary();
                syncRow(row, true);
            });
        });

        refreshSummary();
    }

    function setupRealtimeWaitlist() {
        var panel = document.querySelector("[data-waitlist-panel]");
        if (!panel) {
            return;
        }

        var rows = Array.prototype.slice.call(panel.querySelectorAll("[data-waitlist-row]"));
        if (!rows.length) {
            return;
        }

        var totalTargets = Array.prototype.slice.call(panel.querySelectorAll("[data-waitlist-total]"));
        var labels = {
            defaultButton: "C\u1eadp nh\u1eadt",
            invalidButton: "S\u1ed1 l\u01b0\u1ee3ng ch\u01b0a h\u1ee3p l\u1ec7",
            savingButton: "\u0110ang l\u01b0u...",
            savedButton: "\u0110\u00e3 l\u01b0u",
            errorButton: "L\u1ed7i l\u01b0u"
        };

        function parseNumber(value, fallback) {
            var parsed = Number(value);
            return Number.isFinite(parsed) ? parsed : fallback;
        }

        function normalizeQuantity(value) {
            var parsed = parseInt(value, 10);
            if (!Number.isFinite(parsed) || parsed < 1) {
                return 1;
            }
            return parsed;
        }

        function formatAmount(value) {
            if (!Number.isFinite(value)) {
                return "0";
            }

            var rounded = Math.round(value * 100) / 100;
            return Number.isInteger(rounded) ? rounded.toFixed(1) : String(rounded);
        }

        function setButtonState(button, label, disabled, resetDelay) {
            if (!button) {
                return;
            }

            button.textContent = label;
            button.disabled = !!disabled;

            if (button._waitlistResetTimer) {
                window.clearTimeout(button._waitlistResetTimer);
                button._waitlistResetTimer = null;
            }

            if (resetDelay) {
                button._waitlistResetTimer = window.setTimeout(function () {
                    button.textContent = labels.defaultButton;
                    button.disabled = false;
                }, resetDelay);
            }
        }

        function refreshRow(row) {
            var input = row.querySelector("[data-waitlist-qty-input]");
            var lineTotalElement = row.querySelector("[data-waitlist-line-total]");
            var saveButton = row.querySelector("[data-waitlist-save-button]");
            var quantity = normalizeQuantity(input ? input.value : 1);
            var unitPrice = parseNumber(row.getAttribute("data-unit-price"), 0);
            var lineTotal = unitPrice * quantity;

            if (input) {
                input.value = quantity;
            }

            row.dataset.quantity = String(quantity);
            row.dataset.lineTotal = String(lineTotal);

            if (lineTotalElement) {
                lineTotalElement.textContent = formatAmount(lineTotal);
            }

            if (saveButton && !saveButton.dataset.busy) {
                setButtonState(saveButton, labels.defaultButton, false);
            }

            return {
                lineTotal: lineTotal
            };
        }

        function refreshSummary() {
            var totalAmount = 0;

            rows.forEach(function (row) {
                totalAmount += refreshRow(row).lineTotal;
            });

            totalTargets.forEach(function (target) {
                target.textContent = formatAmount(totalAmount);
            });
        }

        function clearPendingSync(row) {
            if (row._waitlistSyncTimer) {
                window.clearTimeout(row._waitlistSyncTimer);
                row._waitlistSyncTimer = null;
            }
        }

        function syncRow(row, immediate) {
            var formId = row.getAttribute("data-update-form-id");
            var form = formId ? document.getElementById(formId) : null;
            var input = row.querySelector("[data-waitlist-qty-input]");
            var saveButton = row.querySelector("[data-waitlist-save-button]");
            var quantity = normalizeQuantity(input ? input.value : 1);

            clearPendingSync(row);

            if (!form || quantity < 1) {
                if (saveButton) {
                    setButtonState(saveButton, labels.invalidButton, true);
                }
                return;
            }

            if (row.dataset.lastSyncedQty === String(quantity)) {
                return;
            }

            function sendUpdate() {
                var payload = new URLSearchParams(new FormData(form));
                var controller = typeof AbortController === "function" ? new AbortController() : null;

                if (row._waitlistController) {
                    row._waitlistController.abort();
                }
                row._waitlistController = controller;

                payload.set("quantity", String(quantity));

                if (saveButton) {
                    saveButton.dataset.busy = "true";
                    setButtonState(saveButton, labels.savingButton, true);
                }

                fetch(form.action, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
                        "X-Requested-With": "XMLHttpRequest"
                    },
                    body: payload.toString(),
                    signal: controller ? controller.signal : undefined
                })
                        .then(function (response) {
                            if (!response.ok) {
                                throw new Error("sync_failed");
                            }

                            row.dataset.lastSyncedQty = String(quantity);
                            if (saveButton) {
                                setButtonState(saveButton, labels.savedButton, false, 1200);
                            }
                        })
                        .catch(function (error) {
                            if (error && error.name === "AbortError") {
                                return;
                            }

                            if (saveButton) {
                                setButtonState(saveButton, labels.errorButton, false, 1600);
                            }
                        })
                        .finally(function () {
                            if (saveButton) {
                                delete saveButton.dataset.busy;
                            }
                        });
            }

            if (immediate) {
                sendUpdate();
            } else {
                row._waitlistSyncTimer = window.setTimeout(sendUpdate, 450);
            }
        }

        rows.forEach(function (row) {
            var formId = row.getAttribute("data-update-form-id");
            var form = formId ? document.getElementById(formId) : null;
            var input = row.querySelector("[data-waitlist-qty-input]");

            refreshRow(row);
            row.dataset.lastSyncedQty = row.dataset.quantity || "1";

            if (!input || !form) {
                return;
            }

            input.addEventListener("input", function () {
                refreshRow(row);
                refreshSummary();
                syncRow(row, false);
            });

            input.addEventListener("change", function () {
                refreshRow(row);
                refreshSummary();
                syncRow(row, true);
            });

            form.addEventListener("submit", function (event) {
                event.preventDefault();
                refreshRow(row);
                refreshSummary();
                syncRow(row, true);
            });
        });

        refreshSummary();
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupStudentNavigation();
        setupSelectAll();
        setupStudentCartMenu();
        setupRealtimeBuyList();
        setupRealtimeWaitlist();
    });
}());
