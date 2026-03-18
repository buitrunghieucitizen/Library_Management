<%@ page pageEncoding="UTF-8" %>
            </main>
        </div>
    </div>

    <c:if test="${enableCharts}">
        <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
    </c:if>
    <script src="${pageContext.request.contextPath}/assets/js/admin-shared.js"></script>
</body>
</html>
