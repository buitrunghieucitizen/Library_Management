<%@ page pageEncoding="UTF-8" %>
</main>
</div>
</div>

<c:if test="${enableCharts}">
    <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
</c:if>
<script src="${pageContext.request.contextPath}/assets/js/admin-shared.js"></script>
<%-- WebSocket admin notification (global) --%>
<script>
    (function () {
        var protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
        var wsUrl = protocol + '//' + location.host + '${pageContext.request.contextPath}/ws/notify/admin';
        var ws = null;
        var delay = 2000;
        function connect() {
            try {
                ws = new WebSocket(wsUrl);
            } catch (e) {
                return;
            }
            ws.onmessage = function (e) {
                try {
                    var d = JSON.parse(e.data);
                    if (d.message) {
                        var c = document.querySelector('.ws-toast-container');
                        if (!c) {
                            c = document.createElement('div');
                            c.className = 'ws-toast-container';
                            c.style.cssText = 'position:fixed;top:16px;right:16px;z-index:99999;';
                            document.body.appendChild(c);
                        }
                        var t = document.createElement('div');
                        t.style.cssText = 'padding:12px 16px;border-radius:10px;color:#fff;background:#2563eb;font-size:13px;margin-bottom:8px;box-shadow:0 4px 12px rgba(0,0,0,.15);';
                        t.textContent = d.message;
                        c.appendChild(t);
                        setTimeout(function () {
                            t.remove();
                        }, 6000);
                    }
                } catch (x) {
                }
            };
            ws.onclose = function () {
                setTimeout(connect, delay);
                delay = Math.min(delay * 1.5, 30000);
            };
            ws.onerror = function () {
                ws.close();
            };
        }
        connect();
    })();
</script>
</body>
</html>
