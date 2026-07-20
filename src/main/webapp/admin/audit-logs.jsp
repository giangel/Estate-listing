<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    
    // FIX: Renamed local variable from 'page' to 'pageNum' to avoid conflict with implicit JSP object 'page'
    int pageNum = 1;
    try { 
        pageNum = Integer.parseInt(request.getParameter("page")); 
    } catch (Exception ignored) {}
    if (pageNum < 1) pageNum = 1;

    int limit  = 20;
    int offset = (pageNum - 1) * limit;

    com.realestate.dao.AuditLogDAO logDAO =
        new com.realestate.dao.AuditLogDAO();
    java.util.List<com.realestate.model.AuditLog> logs =
        logDAO.findRecent(limit, offset);
    int totalLogs  = logDAO.countTotal();
    int totalPages = (int) Math.ceil((double) totalLogs / limit);

    request.setAttribute("logs",        logs);
    request.setAttribute("totalLogs",   totalLogs);
    request.setAttribute("currentPage", pageNum);
    request.setAttribute("totalPages",  totalPages);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <%-- Aligned with your assets deployment directory mapping --%>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<jsp:include page="/includes/navbar.jsp" />

<div class="re-dashboard">
  <jsp:include page="/includes/admin-sidebar.jsp" />
  
  <main class="re-dashboard-content">

    <div class="dashboard-page-header">
      <div>
        <h2><i class="bi bi-journal-text me-2"></i>Audit Logs</h2>
        <p class="text-muted mb-0">
          <strong>${totalLogs}</strong> events recorded.
          Showing page ${currentPage} of ${totalPages}.
        </p>
      </div>
    </div>

    <div class="re-table-wrapper">
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>#</th>
              <th>User</th>
              <th>Action</th>
              <th>Entity</th>
              <th>Description</th>
              <th>IP Address</th>
              <th>Timestamp</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="log" items="${logs}">
              <tr>
                <td style="font-size:0.78rem;color:var(--re-gray-500)">
                  ${log.logId}
                </td>
                <td style="font-size:0.82rem">
                  <c:out value="${not empty log.userName
                                 ? log.userName : 'System'}"/>
                </td>
                <td>
                  <code style="background:rgba(26,60,94,0.08);
                               padding:2px 7px;border-radius:5px;
                               font-size:0.72rem;
                               color:var(--re-primary)">
                    <c:out value="${log.action}"/>
                  </code>
                </td>
                <td style="font-size:0.8rem">
                  <c:out value="${log.entityType}"/>
                  <c:if test="${log.entityId > 0}">
                    #${log.entityId}
                  </c:if>
                </td>
                <td style="font-size:0.8rem;color:var(--re-gray-700);
                           max-width:240px">
                  <c:out value="${log.description}"/>
                </td>
                <td style="font-size:0.78rem;color:var(--re-gray-500)">
                  <c:out value="${log.ipAddress}"/>
                </td>
                <td style="font-size:0.78rem;color:var(--re-gray-500);
                           white-space:nowrap">
                  <fmt:formatDate value="${log.createdAt}"
                                  pattern="dd/MM/yy HH:mm:ss"/>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <c:if test="${totalPages > 1}">
        <div class="re-pagination p-3">
          <c:if test="${currentPage > 1}">
            <a class="page-btn"
               href="<%= ctx %>/admin/audit-logs.jsp?page=${currentPage - 1}">
              <i class="bi bi-chevron-left"></i>
            </a>
          </c:if>
          <c:forEach begin="1" end="${totalPages}" var="pg">
            <c:if test="${pg >= currentPage - 2 and pg <= currentPage + 2}">
              <a class="page-btn ${pg == currentPage ? 'active' : ''}"
                 href="<%= ctx %>/admin/audit-logs.jsp?page=${pg}">
                ${pg}
              </a>
            </c:if>
          </c:forEach>
          <c:if test="${currentPage < totalPages}">
            <a class="page-btn"
               href="<%= ctx %>/admin/audit-logs.jsp?page=${currentPage + 1}">
              <i class="bi bi-chevron-right"></i>
            </a>
          </c:if>
        </div>
      </c:if>

    </div>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>