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
      <div>
        <h2>
          <i class="bi bi-shield-exclamation me-2"
             style="color:#dc3545"></i>
          Fraud Reports
        </h2>
        <p class="text-muted mb-0">
          <strong>${openCount}</strong> open reports require attention.
        </p>
      </div>
      <div class="d-flex gap-2">
        <a href="<%= ctx %>/admin/fraud"
           class="btn btn-sm ${empty statusFilter ? 'btn-danger' : 'btn-outline-secondary'}">
          All
        </a>
        <a href="<%= ctx %>/admin/fraud?status=OPEN"
           class="btn btn-sm ${statusFilter == 'OPEN' ? 'btn-danger' : 'btn-outline-secondary'}">
          Open
        </a>
        <a href="<%= ctx %>/admin/fraud?status=INVESTIGATING"
           class="btn btn-sm ${statusFilter == 'INVESTIGATING' ? 'btn-danger' : 'btn-outline-secondary'}">
          Investigating
        </a>
        <a href="<%= ctx %>/admin/fraud?status=RESOLVED"
           class="btn btn-sm ${statusFilter == 'RESOLVED' ? 'btn-success' : 'btn-outline-secondary'}">
          Resolved
        </a>
      </div>
    </div>

    <%@ include file="/includes/alerts.jsp" %>

    <div class="re-table-wrapper">
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Property</th>
              <th>Reporter</th>
              <th>Reason</th>
              <th>Details</th>
              <th>Status</th>
              <th>Filed</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty fraudReports}">
                <c:forEach var="fr" items="${fraudReports}">
                  <tr>
                    <td style="font-size:0.8rem;color:var(--re-gray-500)">
                      ${fr.reportId}
                    </td>
                    <td style="font-size:0.875rem;font-weight:600">
                      <a href="<%= ctx %>/property?id=${fr.propertyId}"
                         target="_blank"
                         style="color:var(--re-primary)">
                        <c:out value="${fr.propertyTitle}"/>
                      </a>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${fr.reporterName}"/>
                    </td>
                    <td>
                      <span class="re-badge re-badge-pending"
                            style="font-size:0.72rem">
                        <c:out value="${fr.reportReason}"/>
                      </span>
                    </td>
                    <td style="font-size:0.8rem;max-width:180px;
                               color:var(--re-gray-700)">
                      <c:out value="${fr.reportDetails}"/>
                    </td>
                    <td>
                      <span class="re-badge ${fr.statusBadgeClass}"
                            style="font-size:0.72rem">
                        <c:out value="${fr.reportStatus}"/>
                      </span>
                    </td>
                    <td style="font-size:0.78rem;color:var(--re-gray-500)">
                      <fmt:formatDate value="${fr.createdAt}"
                                      pattern="dd/MM/yy"/>
                    </td>
                    <td>
                      <c:if test="${fr.reportStatus != 'RESOLVED'
                                    and fr.reportStatus != 'DISMISSED'}">
                        <button class="btn btn-sm btn-outline-brand"
                                style="padding:3px 8px;font-size:0.75rem"
                                data-bs-toggle="modal"
                                data-bs-target="#resolveModal"
                                onclick="setResolveTarget(
                                  ${fr.reportId})">
                          Update
                        </button>
                      </c:if>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="8" class="text-center py-4 text-muted">
                    No fraud reports found.
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

<!-- Resolve Modal -->
<div class="modal fade" id="resolveModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content rounded-4">
      <div class="modal-header border-0 pb-0">
        <h5 class="modal-title fw-bold">Update Fraud Report</h5>
        <button type="button" class="btn-close"
                data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <form action="<%= ctx %>/admin/fraud" method="POST">
          <input type="hidden" name="_csrf"
                 value="${sessionScope.csrfToken}">
          <input type="hidden" name="reportId" id="resolveReportId">
          <div class="re-form-group">
            <label>New Status <span class="required">*</span></label>
            <select class="re-form-control" name="status" required>
              <option value="INVESTIGATING">Investigating</option>
              <option value="RESOLVED">Resolved</option>
              <option value="DISMISSED">Dismissed (No Action)</option>
            </select>
          </div>
          <div class="re-form-group">
            <label>Resolution Notes</label>
            <textarea class="re-form-control" name="resolutionNotes"
                      rows="3"
                      placeholder="Describe actions taken..."></textarea>
          </div>
          <button type="submit" class="btn btn-primary-brand w-100">
            <i class="bi bi-check-circle me-1"></i> Update Report
          </button>
        </form>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script>
function setResolveTarget(reportId) {
  document.getElementById('resolveReportId').value = reportId;
}
</script>
</body>
</html>