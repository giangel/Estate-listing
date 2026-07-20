<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="re-dashboard">
  <jsp:include page="/includes/admin-sidebar.jsp" />
  <main class="re-dashboard-content">

    <div class="dashboard-page-header">
      <h2><i class="bi bi-patch-check me-2"></i>Agent Verification</h2>
    </div>

    <%@ include file="/includes/alerts.jsp" %>

    <div class="re-table-wrapper">
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>Agent Name</th>
              <th>Email</th>
              <th>Agency</th>
              <th>License #</th>
              <th>Status</th>
              <th>Registered</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty agents}">
                <c:forEach var="agent" items="${agents}">
                  <tr>
                    <td style="font-weight:600;font-size:0.875rem">
                      <c:out value="${agent.fullName}"/>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${agent.email}"/>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${not empty agent.agencyName
                                     ? agent.agencyName : '-'}"/>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${not empty agent.licenseNumber
                                     ? agent.licenseNumber : '-'}"/>
                    </td>
                    <td>
                      <span class="re-badge
                        ${agent.verificationStatus == 'VERIFIED'
                            ? 're-badge-available' :
                          agent.verificationStatus == 'REJECTED'
                            ? 're-badge-occupied' :
                            're-badge-pending'}"
                            style="font-size:0.72rem">
                        <c:out value="${agent.verificationStatus}"/>
                      </span>
                    </td>
                    <td style="font-size:0.78rem;color:var(--re-gray-500)">
                      <fmt:formatDate value="${agent.userCreated}"
                                      pattern="dd MMM yyyy"/>
                    </td>
                    <td>
                      <c:if test="${agent.verificationStatus == 'PENDING'}">
                        <div class="d-flex gap-1">
                          <form action="<%= ctx %>/admin/verify-agent"
                                method="POST" style="display:inline">
                            <input type="hidden" name="_csrf"
                                   value="${sessionScope.csrfToken}">
                            <input type="hidden" name="agentId"
                                   value="${agent.agentId}">
                            <input type="hidden" name="action"
                                   value="verify">
                            <button type="submit"
                                    class="btn btn-sm"
                                    style="background:#198754;color:white;
                                           padding:4px 12px;font-size:0.78rem"
                                    onclick="return confirm(
                                      'Verify this agent account?')">
                              <i class="bi bi-patch-check me-1"></i>
                              Verify
                            </button>
                          </form>
                          <form action="<%= ctx %>/admin/verify-agent"
                                method="POST" style="display:inline">
                            <input type="hidden" name="_csrf"
                                   value="${sessionScope.csrfToken}">
                            <input type="hidden" name="agentId"
                                   value="${agent.agentId}">
                            <input type="hidden" name="action"
                                   value="reject">
                            <button type="submit"
                                    class="btn btn-sm"
                                    style="background:#dc3545;color:white;
                                           padding:4px 12px;font-size:0.78rem"
                                    onclick="return confirm(
                                      'Reject this agent?')">
                              <i class="bi bi-x-circle me-1"></i>
                              Reject
                            </button>
                          </form>
                        </div>
                      </c:if>
                      <c:if test="${agent.verificationStatus != 'PENDING'}">
                        <span style="font-size:0.78rem;color:var(--re-gray-500)">
                          Already processed
                        </span>
                      </c:if>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="7" class="text-center py-4 text-muted">
                    No agent verification requests found.
                  </td>
                </tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>

  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>