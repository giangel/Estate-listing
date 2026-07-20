<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx     = request.getContextPath();
    String idsParam = request.getParameter("ids");
    java.util.List<com.realestate.model.Property> compareList =
        new java.util.ArrayList<>();

    if (idsParam != null && !idsParam.trim().isEmpty()) {
        com.realestate.dao.PropertyDAO pDAO =
            new com.realestate.dao.PropertyDAO();
        for (String idStr : idsParam.split(",")) {
            try {
                int pid = Integer.parseInt(idStr.trim());
                com.realestate.model.Property p = pDAO.findById(pid);
                if (p != null) compareList.add(p);
                if (compareList.size() >= 4) break;
            } catch (NumberFormatException ignored) {}
        }
    }
    request.setAttribute("compareList", compareList);
%>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">

  <div class="d-flex align-items-center justify-content-between mb-4">
    <h2><i class="bi bi-bar-chart-steps me-2"></i>Compare Properties</h2>
    <a href="<%= ctx %>/properties" class="btn btn-outline-brand btn-sm-custom">
      <i class="bi bi-arrow-left me-1"></i> Back to Browse
    </a>
  </div>

  <c:choose>
    <c:when test="${not empty compareList and compareList.size() >= 2}">
      <div class="table-responsive">
        <table class="compare-table w-100">

          <!-- Cover Images Row -->
          <tr>
            <td class="feature-name">Property</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
               <img src="<%= ctx %>${not empty prop.coverImage
            ? '/'.concat(prop.coverImage)
            : '/assets/images/placeholder.jpg'}"
                     alt="<c:out value='${prop.title}'/>"
                     style="width:100%;max-width:180px;height:120px;
                            object-fit:cover;border-radius:10px;
                            margin-bottom:8px">
                <div class="fw-bold" style="font-size:0.875rem">
                  <a href="<%= ctx %>/property?id=${prop.propertyId}"
                     style="color:var(--re-primary)">
                    <c:out value="${prop.title}"/>
                  </a>
                </div>
              </td>
            </c:forEach>
          </tr>

          <!-- Price -->
          <tr>
            <td class="feature-name">Annual Rent</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center fw-bold"
                  style="color:var(--re-secondary);font-size:1.1rem">
                <c:out value="${prop.formattedPrice}"/>
              </td>
            </c:forEach>
          </tr>

          <!-- Type -->
          <tr>
            <td class="feature-name">Property Type</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <c:out value="${prop.typeName}"/>
              </td>
            </c:forEach>
          </tr>

          <!-- Campus Distance -->
          <tr>
            <td class="feature-name">Campus Distance</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <span class="re-badge re-badge-distance">
                  <i class="${prop.campusDistanceIcon}"></i>
                  <c:out value="${prop.campusDistanceLabel}"/>
                </span>
              </td>
            </c:forEach>
          </tr>

          <!-- Bedrooms -->
          <tr>
            <td class="feature-name">Bedrooms</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">${prop.bedrooms}</td>
            </c:forEach>
          </tr>

          <!-- Bathrooms -->
          <tr>
            <td class="feature-name">Bathrooms</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">${prop.bathrooms}</td>
            </c:forEach>
          </tr>

          <!-- Water -->
          <tr>
            <td class="feature-name">Water Supply</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <span class="quality-badge ${prop.waterQualityClass}">
                  <c:out value="${prop.waterAvailability}"/>
                </span>
              </td>
            </c:forEach>
          </tr>

          <!-- Electricity -->
          <tr>
            <td class="feature-name">Electricity</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <span class="quality-badge ${prop.electricityQualityClass}">
                  <c:out value="${prop.electricity}"/>
                </span>
              </td>
            </c:forEach>
          </tr>

          <!-- Security -->
          <tr>
            <td class="feature-name">Security</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <span class="quality-badge ${prop.securityQualityClass}">
                  <c:out value="${prop.securityLevel}"/>
                </span>
              </td>
            </c:forEach>
          </tr>

          <!-- Road -->
          <tr>
            <td class="feature-name">Road Access</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <span class="quality-badge ${prop.roadQualityClass}">
                  <c:out value="${prop.roadAccessibility}"/>
                </span>
              </td>
            </c:forEach>
          </tr>

          <!-- Internet -->
          <tr>
            <td class="feature-name">Internet</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <c:choose>
                  <c:when test="${prop.internetAvailable}">
                    <i class="bi bi-wifi check-yes"></i> Yes
                  </c:when>
                  <c:otherwise>
                    <i class="bi bi-wifi-off check-no"></i> No
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
          </tr>

          <!-- Fenced -->
          <tr>
            <td class="feature-name">Fenced</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <c:choose>
                  <c:when test="${prop.fenced}">
                    <i class="bi bi-check-circle-fill check-yes"></i> Yes
                  </c:when>
                  <c:otherwise>
                    <i class="bi bi-x-circle-fill check-no"></i> No
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
          </tr>

          <!-- Verified -->
          <tr>
            <td class="feature-name">Verified</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <c:choose>
                  <c:when test="${prop.verified}">
                    <i class="bi bi-patch-check-fill check-yes"></i>
                    Verified
                  </c:when>
                  <c:otherwise>
                    <i class="bi bi-patch-question check-no"></i>
                    Unverified
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
          </tr>

          <!-- Rating -->
          <tr>
            <td class="feature-name">Avg. Rating</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <c:choose>
                  <c:when test="${prop.totalRatings > 0}">
                    <span style="color:#f5a623;font-weight:700">
                      ★ ${prop.averageRating}
                    </span>
                    <span style="font-size:0.75rem;color:var(--re-gray-500)">
                      (${prop.totalRatings})
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span style="color:var(--re-gray-500)">No ratings</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
          </tr>

          <!-- Action Row -->
          <tr>
            <td class="feature-name">Action</td>
            <c:forEach var="prop" items="${compareList}">
              <td class="text-center">
                <a href="<%= ctx %>/property?id=${prop.propertyId}"
                   class="btn btn-primary-brand btn-sm-custom w-100">
                  View Details
                </a>
              </td>
            </c:forEach>
          </tr>

        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">
        <div class="empty-icon"><i class="bi bi-bar-chart-steps"></i></div>
        <h4>No Properties to Compare</h4>
        <p>
          Select at least 2 properties from the browse page to compare them.
        </p>
        <a href="<%= ctx %>/properties" class="btn btn-primary-brand">
          <i class="bi bi-search me-2"></i> Browse Properties
        </a>
      </div>
    </c:otherwise>
  </c:choose>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>