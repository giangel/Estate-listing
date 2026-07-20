<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx     = request.getContextPath();
    int    agentId = (Integer) session.getAttribute("userId");
    com.realestate.dao.PropertyDAO pDAO =
        new com.realestate.dao.PropertyDAO();
    java.util.List<com.realestate.model.Property> agentProps =
        pDAO.findByOwner(agentId);
    request.setAttribute("agentProperties", agentProps);
%>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">

  <div class="dashboard-page-header">
    <h2>
      <i class="bi bi-people-fill me-2"></i>Manage Client Properties
    </h2>
    <a href="<%= ctx %>/landlord/create-property"
       class="btn btn-primary-brand btn-sm-custom">
      <i class="bi bi-plus-circle me-1"></i> Add Listing
    </a>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="re-table-wrapper">
    <div class="table-responsive">
      <table class="re-table">
        <thead>
          <tr>
            <th>Cover</th>
            <th>Property Title</th>
            <th>Type</th>
            <th>Price</th>
            <th>Status</th>
            <th>Views</th>
            <th>Inquiries</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${not empty agentProperties}">
              <c:forEach var="prop" items="${agentProperties}">
                <tr>
                  <td>
                  <img src="<%= ctx %>${not empty prop.coverImage
            ? '/'.concat(prop.coverImage)
            : '/assets/images/placeholder.jpg'}"
                         style="width:50px;height:38px;object-fit:cover;
                                border-radius:6px">
                  </td>
                  <td style="font-size:0.875rem;font-weight:600">
                    <a href="<%= ctx %>/property?id=${prop.propertyId}"
                       target="_blank">
                      <c:out value="${prop.title}"/>
                    </a>
                  </td>
                  <td style="font-size:0.82rem">
                    <c:out value="${prop.typeName}"/>
                  </td>
                  <td style="font-weight:700;color:var(--re-secondary)">
                    <c:out value="${prop.formattedPrice}"/>
                  </td>
                  <td>
                    <span class="re-badge ${prop.statusBadgeClass}"
                          style="font-size:0.72rem">
                      <c:out value="${prop.propertyStatus}"/>
                    </span>
                  </td>
                  <td>${prop.viewCount}</td>
                  <td>${prop.inquiryCount}</td>
                  <td>
                    <div class="d-flex gap-1">
                      <a href="<%= ctx %>/landlord/update-property?id=${prop.propertyId}"
                         class="btn btn-sm btn-outline-brand"
                         style="padding:3px 8px;font-size:0.75rem">
                        <i class="bi bi-pencil"></i>
                      </a>
                      <form action="<%= ctx %>/landlord/delete-property"
                            method="POST" style="display:inline"
                            onsubmit="return confirm('Delete this listing?')">
                        <input type="hidden" name="_csrf"
                               value="${sessionScope.csrfToken}">
                        <input type="hidden" name="propertyId"
                               value="${prop.propertyId}">
                        <button type="submit"
                                class="btn btn-sm"
                                style="background:#dc3545;color:white;
                                       padding:3px 8px;font-size:0.75rem">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                    </div>
                  </td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td colspan="8" class="text-center py-4 text-muted">
                  No properties listed yet.
                  <a href="<%= ctx %>/landlord/add-property.jsp">
                    Add your first listing.
                  </a>
                </td>
              </tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
</body>
</html>