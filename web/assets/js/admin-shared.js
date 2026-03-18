(function () {
    function setupSidebar() {
        var shell = document.getElementById("dashboardShell");
        var overlay = document.getElementById("dashboardOverlay");
        var toggle = document.getElementById("sidebarToggle");

        if (!shell || !overlay || !toggle) {
            return;
        }

        function closeSidebar() {
            shell.classList.remove("sidebar-open");
        }

        toggle.addEventListener("click", function () {
            shell.classList.toggle("sidebar-open");
        });

        overlay.addEventListener("click", closeSidebar);

        window.addEventListener("resize", function () {
            if (window.innerWidth > 1024) {
                closeSidebar();
            }
        });
    }

    function createBorrowBuyChartOptions() {
        return {
            series: [
                {
                    name: "Mượn",
                    data: [44, 55, 57, 56, 61, 58, 63, 60, 66, 50, 72, 80]
                },
                {
                    name: "Mua",
                    data: [76, 85, 101, 98, 87, 105, 91, 114, 94, 86, 99, 110]
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
                categories: ["Th.1", "Th.2", "Th.3", "Th.4", "Th.5", "Th.6", "Th.7", "Th.8", "Th.9", "Th.10", "Th.11", "Th.12"],
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
                        return value + " cuốn";
                    }
                },
                title: {
                    text: "Số lượng (cuốn)",
                    style: { fontWeight: 500 }
                }
            },
            fill: { opacity: 1 },
            tooltip: {
                y: {
                    formatter: function (value) {
                        return value + " cuốn";
                    }
                }
            }
        };
    }

    function setupBorrowBuyChart() {
        var chartContainer = document.getElementById("borrowBuyChart");

        if (!chartContainer || typeof ApexCharts === "undefined") {
            return;
        }

        var chart = new ApexCharts(chartContainer, createBorrowBuyChartOptions());
        chart.render();
    }

    document.addEventListener("DOMContentLoaded", function () {
        setupSidebar();
        setupBorrowBuyChart();
    });
}());
