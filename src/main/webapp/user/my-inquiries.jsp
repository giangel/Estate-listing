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

<div class="container py-4">

  <div class="dashboard-page-header">
    <div>
      <h2><i class="bi bi-envelope me-2"></i>My Inquiries</h2>
      <p class="text-muted mb-0">
        <c:choose>
          <c:when test="${viewMode == 'owner'}">
            Inquiries received on your properties.
          </c:when>
          <c:otherwise>
            Inquiries you have sent to property owners.
          </c:otherwise>
        </c:choose>
      </p>
    </div>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="re-table-wrapper">
    <c:choose>
      <c:when test="${not empty inquiries}">
        <div class="table-responsive">
          <table class="re-table">
            <thead>
              <tr>
                <th>Property</th>
                <c:choose>
                  <c:when test="${viewMode == 'owner'}">
                    <th>From</th>
                  </c:when>
                  <c:otherwise>
                    <th>Owner</th>
                  </c:otherwise>
                </c:choose>
                <th>Message</th>
                <th>Status</th>
                <th>Date</th>
                <c:if test="${viewMode == 'owner'}">
                  <th>Action</th>
                </c:if>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="inq" items="${inquiries}">
                <tr>
                  <td style="font-size:0.875rem;font-weight:600;max-width:160px">
                    <a href="<%= ctx %>/property?id=${inq.propertyId}"
                       target="_blank" style="color:var(--re-primary)">
                      <c:out value="${inq.propertyTitle}"/>
                    </a>
                  </td>
                  <td style="font-size:0.82rem">
                    <c:choose>
                      <c:when test="${viewMode == 'owner'}">
                        <c:out value="${inq.senderName}"/>
                        <div style="font-size:0.75rem;color:var(--re-gray-500)">
                          <c:out value="${inq.senderPhone}"/>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${inq.ownerName}"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td style="max-width:220px">
                    <p style="font-size:0.82rem;margin:0;
                              display:-webkit-box;-webkit-line-clamp:2;
                              -webkit-box-orient:vertical;overflow:hidden">
                      <c:out value="${inq.message}"/>
                    </p>
                    <c:if test="${not empty inq.replyMessage}">
                      <div class="mt-1 p-2 rounded"
                           style="background:rgba(25,135,84,0.06);
                                  font-size:0.78rem">
                        <strong style="color:#198754">Reply:</strong>
                        <c:out value="${inq.replyMessage}"/>
                      </div>
                    </c:if>
                  </td>
                  <td>
                    <span class="re-badge
                      ${inq.inquiryStatus == 'UNREAD' ? 're-badge-pending' :
                        inq.inquiryStatus == 'REPLIED' ? 're-badge-available' :
                        're-badge-distance'}"
                          style="font-size:0.72rem">
                      <c:out value="${inq.inquiryStatus}"/>
                    </span>
                  </td>
                  <td style="font-size:0.78rem;color:var(--re-gray-500)">
                    <fmt:formatDate value="${inq.createdAt}"
                                    pattern="dd MMM yyyy"/>
                  </td>
                  <c:if test="${viewMode == 'owner'}">
                    <td>
                      <c:if test="${inq.inquiryStatus != 'REPLIED'}">
                        <button class="btn btn-sm btn-outline-brand"
                                style="padding:3px 10px;font-size:0.78rem"
                                data-bs-toggle="modal"
                                data-bs-target="#replyModal"
                                onclick="setReplyTarget(
                                  ${inq.inquiryId},
                                  '<c:out value="${inq.senderName}"/>')">
                          <i class="bi bi-reply"></i> Reply
                        </button>
                      </c:if>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:when>
      <c:otherwise>
        <div class="empty-state py-4">
          <div class="empty-icon"><i class="bi bi-envelope-open"></i></div>
          <h5 class="mt-3">No Inquiries Yet</h5>
          <p class="text-muted">
            <c:choose>
              <c:when test="${viewMode == 'owner'}">
                No one has sent an inquiry about your properties yet.
              </c:when>
              <c:otherwise>
                You have not sent any inquiries yet.
                Browse properties to get started.
              </c:otherwise>
            </c:choose>
          </p>
        </div>
      </c:otherwise>
    </c:choose>
  </div>

</div>

<!-- Reply Modal -->
<div class="modal fade" id="replyModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content rounded-4">
      <div class="modal-header border-0 pb-0">
        <h5 class="modal-title fw-bold">
          Reply to Inquiry
        </h5>
        <button type="button" class="btn-close"
                data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <p class="text-muted" style="font-size:0.875rem">
          Replying to: <strong id="replyTargetName"></strong>
        </p>
        <form action="<%= ctx %>/user/inquiry" method="POST">
          <input type="hidden" name="_csrf"
                 value="${sessionScope.csrfToken}">
          <input type="hidden" name="action" value="reply">
          <input type="hidden" name="inquiryId" id="replyInquiryId">
          <div class="re-form-group">
            <label>Your Reply <span class="required">*</span></label>
            <textarea class="re-form-control" name="reply"
                      rows="4" required
                      placeholder="Type your response here..."></textarea>
          </div>
          <button type="submit" class="btn btn-primary-brand w-100">
            <i class="bi bi-send me-1"></i> Send Reply
          </button>
        </form>
      </div>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script>
function setReplyTarget(inquiryId, senderName) {
  document.getElementById('replyInquiryId').value   = inquiryId;
  document.getElementById('replyTargetName').textContent = senderName;
}
</script>
</body>
</html>