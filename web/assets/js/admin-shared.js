(function () {
    function setupSidebar() {
        var shell = document.getElementById("dashboardShell");
        var overlay = document.getElementById("dashboardOverlay");
        var toggle = document.getElementById("sidebarToggle");
        var storageKey = "library-manager-admin-sidebar-collapsed";

        if (!shell || !overlay || !toggle) {
            return;
        }

        function isDesktop() {
            return window.innerWidth > 1024;
        }

        function readCollapsedState() {
            try {
                return window.localStorage.getItem(storageKey) === "true";
            } catch (error) {
                return false;
            }
        }

        function writeCollapsedState(isCollapsed) {
            try {
                window.localStorage.setItem(storageKey, isCollapsed ? "true" : "false");
            } catch (error) {
                return;
            }
        }

        function closeSidebar() {
            shell.classList.remove("sidebar-open");
        }

        function syncToggleState() {
            var isCollapsed = shell.classList.contains("sidebar-collapsed");
            var isOpen = shell.classList.contains("sidebar-open");
            var label = isDesktop()
                ? (isCollapsed ? "M\u1edf r\u1ed9ng menu \u0111i\u1ec1u h\u01b0\u1edbng" : "Thu g\u1ecdn menu \u0111i\u1ec1u h\u01b0\u1edbng")
                : (isOpen ? "\u0110\u00f3ng menu \u0111i\u1ec1u h\u01b0\u1edbng" : "M\u1edf menu \u0111i\u1ec1u h\u01b0\u1edbng");

            toggle.setAttribute("aria-label", label);
            toggle.setAttribute("title", label);
            toggle.setAttribute("aria-expanded", isDesktop() ? String(!isCollapsed) : String(isOpen));
        }

        function applySidebarMode() {
            if (isDesktop()) {
                closeSidebar();
                shell.classList.toggle("sidebar-collapsed", readCollapsedState());
            } else {
                shell.classList.remove("sidebar-collapsed");
            }

            syncToggleState();
        }

        toggle.addEventListener("click", function () {
            if (isDesktop()) {
                var nextCollapsed = !shell.classList.contains("sidebar-collapsed");
                shell.classList.toggle("sidebar-collapsed", nextCollapsed);
                writeCollapsedState(nextCollapsed);
                syncToggleState();
                return;
            }

            shell.classList.toggle("sidebar-open");
            syncToggleState();
        });

        overlay.addEventListener("click", function () {
            closeSidebar();
            syncToggleState();
        });

        window.addEventListener("resize", applySidebarMode);

        applySidebarMode();
    }

    function parseChartPayload() {
        var payloadElement = document.getElementById("borrowBuyChartData");

        if (!payloadElement) {
            return null;
        }

        try {
            return JSON.parse(payloadElement.textContent);
        } catch (error) {
            return null;
        }
    }

    function getChartRange(payload, rangeKey) {
        if (!payload || !payload.ranges || !payload.ranges[rangeKey]) {
            return null;
        }

        return payload.ranges[rangeKey];
    }

    function createBorrowBuyChartOptions(payload, rangeKey) {
        var range = getChartRange(payload, rangeKey);

        if (!range) {
            return null;
        }

        return {
            series: [
                {
                    name: payload.seriesNames.borrow,
                    data: range.borrowData
                },
                {
                    name: payload.seriesNames.order,
                    data: range.orderData
                }
            ],
            colors: ["#4ade80", "#3b82f6"],
            chart: {
                type: "bar",
                height: 350,
                width: "100%",
                parentHeightOffset: 0,
                toolbar: { show: false },
                fontFamily: "inherit"
            },
            grid: {
                show: true,
                borderColor: "#e2e8f0"
            },
            legend: {
                show: true,
                fontWeight: 500,
                markers: {
                    size: 5,
                    shape: "square",
                    strokeWidth: 0,
                    offsetX: -2,
                    offsetY: 0
                }
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: "85%",
                    borderRadius: 3,
                    borderRadiusApplication: "end"
                }
            },
            dataLabels: { enabled: false },
            stroke: {
                show: false,
                width: 2,
                colors: ["transparent"]
            },
            xaxis: {
                categories: range.categories,
                labels: {
                    rotate: range.categories.length > 12 ? -45 : 0,
                    hideOverlappingLabels: true,
                    trim: false
                },
                axisBorder: {
                    show: false,
                    color: "#e2e8f0",
                    height: 1,
                    width: "100%",
                    offsetX: 0,
                    offsetY: 0
                },
                axisTicks: {
                    show: false,
                    borderType: "solid",
                    color: "#e2e8f0",
                    height: 6,
                    offsetX: 0,
                    offsetY: 0
                }
            },
            yaxis: {
                labels: {
                    formatter: function (value) {
                        return value + " " + payload.unitLabel;
                    }
                },
                title: {
                    text: payload.yAxisTitle,
                    style: { fontWeight: 500 }
                }
            },
            fill: { opacity: 1 },
            tooltip: {
                y: {
                    formatter: function (value) {
                        return value + " " + payload.unitLabel;
                    }
                }
            }
        };
    }

    function setupBorrowBuyChart() {
        var chartContainer = document.getElementById("borrowBuyChart");
        var filter = document.getElementById("borrowBuyChartFilter");
        var payload = parseChartPayload();
        var defaultRange = payload && payload.defaultRange ? payload.defaultRange : "year";
        var chartOptions;
        var chart;

        if (!chartContainer || typeof ApexCharts === "undefined" || !payload) {
            return;
        }

        function applyChartRange(rangeKey) {
            var range = getChartRange(payload, rangeKey);

            if (!range) {
                return;
            }

            chart.updateOptions({
                xaxis: {
                    categories: range.categories,
                    labels: {
                        rotate: range.categories.length > 12 ? -45 : 0,
                        hideOverlappingLabels: true,
                        trim: false
                    }
                }
            }, false, false, false);

            chart.updateSeries([
                {
                    name: payload.seriesNames.borrow,
                    data: range.borrowData
                },
                {
                    name: payload.seriesNames.order,
                    data: range.orderData
                }
            ], true);
        }

        if (filter) {
            filter.value = defaultRange;
        }

        chartOptions = createBorrowBuyChartOptions(payload, defaultRange);
        if (!chartOptions) {
            return;
        }

        chart = new ApexCharts(chartContainer, chartOptions);
        chart.render();

        if (filter) {
            filter.addEventListener("change", function () {
                applyChartRange(filter.value);
            });
        }
    }

    function setupStaffRoleOptions() {
        var roleInputs = document.querySelectorAll(".staff-role-option input[type='checkbox']");

        if (!roleInputs.length) {
            return;
        }

        function syncRoleOptionState(input) {
            var option = input.closest(".staff-role-option");

            if (!option) {
                return;
            }

            option.classList.toggle("is-selected", input.checked);
        }

        roleInputs.forEach(function (input) {
            syncRoleOptionState(input);
            input.addEventListener("change", function () {
                syncRoleOptionState(input);
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupSidebar();
        setupBorrowBuyChart();
        setupStaffRoleOptions();
    });
}());
