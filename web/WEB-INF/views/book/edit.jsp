<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sua sach</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.2); }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; font-size: 15px; padding: 8px 16px; border-radius: 6px; transition: all 0.2s; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .container { max-width: 680px; margin: 30px auto; padding: 0 20px; }
        .card { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
        h2 { color: #1e3c72; margin-bottom: 20px; }
        h3 { color: #1e3c72; margin-top: 20px; margin-bottom: 12px; font-size: 18px; }
        label { display: block; font-weight: 600; color: #333; margin-bottom: 6px; margin-top: 14px; font-size: 14px; }
        input[type="text"], input[type="number"] { width: 100%; padding: 10px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; transition: border 0.2s; }
        input:focus { outline: none; border-color: #2a5298; }
        .meta { color: #64748b; font-size: 13px; margin-top: 6px; }
        .btn { display: inline-block; padding: 10px 24px; border-radius: 8px; text-decoration: none; font-size: 14px; font-weight: 600; transition: all 0.2s; border: none; cursor: pointer; margin-top: 20px; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-primary:hover { background: #1e3c72; }
        .btn-secondary { background: #6c757d; color: #fff; margin-left: 8px; }
        .btn-secondary:hover { background: #545b62; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/admin/books">Books</a>
    </div>
    <div class="container">
        <div class="card">
            <h2>Sua sach</h2>
            <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=edit">
                <input type="hidden" name="bookID" value="${book.bookID}">

                <label>Ten sach</label>
                <input type="text" name="bookName" value="${book.bookName}" required>

                <label>So luong</label>
                <input type="number" name="quantity" min="0" value="${book.quantity}" required>

                <label>Con lai</label>
                <input type="number" name="available" min="0" value="${book.available}" required>

                <label>Category ID</label>
                <input type="number" name="categoryID" min="1" value="${book.categoryID}" required>

                <label>Publisher ID</label>
                <input type="number" name="publisherID" min="1" value="${book.publisherID}" required>

                <h3>Gia hien hanh</h3>

                <label>Gia</label>
                <input type="number" name="priceAmount" min="0" step="0.01" value="${empty currentPrice ? 0 : currentPrice.amount}" required>

                <label>Tien te</label>
                <input type="text" name="priceCurrency" value="${empty currentPrice ? 'VND' : currentPrice.currency}" required>

                <label>Ghi chu gia</label>
                <input type="text" name="priceNote" value="${empty currentPrice ? '' : currentPrice.note}">
                <p class="meta">
                    <c:choose>
                        <c:when test="${not empty currentPrice}">
                            Gia hien hanh bat dau tu: ${currentPrice.startDate}
                        </c:when>
                        <c:otherwise>
                            Sach nay chua co gia, cap nhat lan nay se tao gia moi.
                        </c:otherwise>
                    </c:choose>
                </p>

                <button class="btn btn-primary" type="submit">Cap nhat</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books">Huy</a>
            </form>
        </div>
    </div>
</body>
</html>
