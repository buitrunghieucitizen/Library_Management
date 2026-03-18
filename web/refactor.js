const fs = require('fs');
const path = require('path');

function walkDir(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walkDir(dirPath, callback) : callback(dirPath);
    });
}

const targetPath = "c:/Users/Administrator/Downloads/New folder/LibraryManager/web/WEB-INF/views";

walkDir(targetPath, function(filePath) {
    if (filePath.endsWith('.jsp') && filePath.indexOf('admin') === -1 && filePath.indexOf('client') === -1) {
        // Skip book/list.jsp as it is shared and needs manual refactoring
        if (filePath.replace(/\\/g, '/').endsWith('book/list.jsp')) {
            return;
        }

        let content = fs.readFileSync(filePath, 'utf-8');
        let originalContent = content;

        if (!content.includes('../admin/_header.jsp')) return;

        // Extract title
        let titleMatch = content.match(/<title>(.*?)<\/title>/);
        let title = titleMatch ? titleMatch[1] : 'Quản trị hệ thống';

        // Extract activeTab
        let tabMatch = content.match(/<c:set var="activeTab" value="(.*?)" \/>/);
        let activeTab = tabMatch ? tabMatch[1] : '';

        // Replace top part
        // We find the index of <%@ include file="../admin/_header.jsp" %>
        let includeIndex = content.indexOf('<%@ include file="../admin/_header.jsp" %>');
        if (includeIndex !== -1) {
            let topPart = content.substring(0, includeIndex + '<%@ include file="../admin/_header.jsp" %>'.length);
            
            let newTop = `<c:set var="pageTitle" value="${title}" />\n`;
            if (activeTab) {
                newTop += `    <c:set var="activeTab" value="${activeTab}" />\n`;
            }
            newTop += `    <%@ include file="../admin/layout/_admin_header.jsp" %>`;
            
            content = content.replace(topPart, newTop);
        }

        // Replace bottom part
        let bottomRegex = /<\/body>[\s\S]*?<\/html>/;
        content = content.replace(bottomRegex, '<%@ include file="../admin/layout/_admin_footer.jsp" %>\n');

        if (content !== originalContent) {
            fs.writeFileSync(filePath, content, 'utf-8');
            console.log("Refactored: " + filePath);
        }
    }
});
