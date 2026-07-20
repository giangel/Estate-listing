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
      <h2><i class="bi bi-calendar-check me-2"></i>Appointments</h2>
      <p class="text-muted mb-0">
        <c:choose>
          <c:when test="${viewMode == 'owner'}">
            Viewing requests for your properties.
          </c:when>
          <c:otherwise>
            Viewing appointments you have requested.
          </c:otherwise>
        </c:choose>
      </p>
    </div>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="re-table-wrapper">
    <c:choose>
      <c:when test="${not empty appointments}">
        <div class="table-responsive">
          <table class="re-table">
            <thead>
              <tr>
                <th>Property</th>
                <c:choose>
                  <c:when test="${viewMode == 'owner'}">
                    <th>Requester</th>
                  </c:when>
                  <c:otherwise>
                    <th>Owner</th>
                  </c:otherwise>
                </c:choose>
                <th>Date</th>
                <th>Time</th>
                <th>Notes</th>
                <th>Status</th>
                <c:if test="${viewMode == 'owner'}">
                  <th>Actions</th>
                </c:if>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="appt" items="${appointments}">
                <tr>
                  <td style="font-size:0.875rem;font-weight:600">
                    <a href="<%= ctx %>/property?id=${appt.propertyId}"
                       target="_blank"
                       style="color:var(--re-primary)">
                      <c:out value="${appt.propertyTitle}"/>
                    </a>
                  </td>
                  <td style="font-size:0.82rem">
                    <c:choose>
                      <c:when test="${viewMode == 'owner'}">
                        <c:out value="${appt.requesterName}"/>
                        <div style="font-size:0.75rem;color:var(--re-gray-500)">
                          <c:out value="${appt.requesterPhone}"/>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${appt.ownerName}"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td style="font-size:0.875rem;font-weight:600;
                             color:var(--re-primary)">
                    <fmt:formatDate value="${appt.preferredDate}"
                                    pattern="dd MMM yyyy"/>
                  </td>
                  <td style="font-size:0.82rem">
                    <c:choose>
                      <c:when test="${not empty appt.preferredTime}">
                        <fmt:formatDate value="${appt.preferredTime}"
                                        pattern="hh:mm a"/>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">Flexible</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td style="font-size:0.8rem;max-width:160px;
                             color:var(--re-gray-700)">
                    <c:out value="${not empty appt.notes
                                   ? appt.notes : '-'}"/>
                  </td>
                  <td>
                    <span class="re-badge
                      ${appt.appointmentStatus == 'CONFIRMED'
                          ? 're-badge-available' :
                        appt.appointmentStatus == 'CANCELLED'
                          ? 're-badge-occupied' :
                        appt.appointmentStatus == 'COMPLETED'
                          ? 're-badge-verified' :
                          're-badge-pending'}"
                          style="font-size:0.72rem">
                      <c:out value="${appt.appointmentStatus}"/>
                    </span>
                  </td>
                  <c:if test="${viewMode == 'owner'}">
                    <td>
                      <c:if test="${appt.appointmentStatus == 'PENDING'}">
                        <div class="d-flex gap-1">
                          <form action="<%= ctx %>/user/appointment"
                                method="POST" style="display:inline">
                            <input type="hidden" name="_csrf"
                                   value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action"
                                   value="updateStatus">
                            <input type="hidden" name="appointmentId"
                                   value="${appt.appointmentId}">
                            <input type="hidden" name="status"
                                   value="CONFIRMED">
                            <button type="submit"
                                    class="btn btn-sm"
                                    style="background:#198754;color:white;
                                           padding:3px 8px;font-size:0.75rem">
                              <i class="bi bi-check"></i> Confirm
                            </button>
                          </form>
                          <form action="<%= ctx %>/user/appointment"
                                method="POST" style="display:inline">
                            <input type="hidden" name="_csrf"
                                   value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action"
                                   value="updateStatus">
                            <input type="hidden" name="appointmentId"
                                   value="${appt.appointmentId}">
                            <input type="hidden" name="status"
                                   value="CANCELLED">
                            <button type="submit"
                                    class="btn btn-sm"
                                    style="background:#dc3545;color:white;
                                           padding:3px 8px;font-size:0.75rem">
                              <i class="bi bi-x"></i> Cancel
                            </button>
                          </form>
                        </div>
                      </c:if>
                      <c:if test="${appt.appointmentStatus == 'CONFIRMED'}">
                        <form action="<%= ctx %>/user/appointment"
                              method="POST">
                          <input type="hidden" name="_csrf"
                                 value="${sessionScope.csrfToken}">
                          <input type="hidden" name="action"
                                 value="updateStatus">
                          <input type="hidden" name="appointmentId"
                                 value="${appt.appointmentId}">
                          <input type="hidden" name="status"
                                 value="COMPLETED">
                          <button type="submit"
                                  class="btn btn-sm btn-outline-brand"
                                  style="padding:3px 8px;font-size:0.75rem">
                            ✓ Mark Done
                          </button>
                        </form>
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
          <div class="empty-icon">
            <i class="bi bi-calendar-x"></i>
          </div>
          <h5 class="mt-3">No Appointments</h5>
          <p class="text-muted">
            <c:choose>
              <c:when test="${viewMode == 'owner'}">
                No viewing requests received yet.
              </c:when>
              <c:otherwise>
                You have not booked any viewings yet.
                Visit a property page to book a viewing.
              </c:otherwise>
            </c:choose>
          </p>
        </div>
      </c:otherwise>
    </c:choose>
  </div>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>