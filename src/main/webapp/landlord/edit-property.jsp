<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
  <nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb" style="font-size:0.82rem">
      <li class="breadcrumb-item">
        <a href="<%= ctx %>/landlord/landlord-dashboard.jsp">Dashboard</a>
      </li>
      <li class="breadcrumb-item">
        <a href="<%= ctx %>/landlord/manage-properties.jsp">My Properties</a>
      </li>
      <li class="breadcrumb-item active">Edit Property</li>
    </ol>
  </nav>

  <div class="row justify-content-center">
    <div class="col-lg-10">
      <div class="card border-0 shadow-sm rounded-4">
        <div class="card-header border-0 pt-4 pb-0 px-4 bg-white">
          <h3 class="mb-0">
            <i class="bi bi-pencil-square me-2"
               style="color:var(--re-secondary)"></i>
            Edit Property
          </h3>
          <p class="text-muted mt-1 mb-0" style="font-size:0.875rem">
            Editing:
            <strong><c:out value="${property.title}"/></strong>
          </p>
        </div>

        <div class="card-body p-4">

          <%@ include file="/includes/alerts.jsp" %>

          <c:if test="${not empty requestScope.errorMessage}">
            <div class="re-alert re-alert-error">
              <i class="bi bi-exclamation-circle-fill"></i>
              <c:out value="${requestScope.errorMessage}"/>
            </div>
          </c:if>

          <form action="<%= ctx %>/landlord/update-property"
                method="POST"
                enctype="multipart/form-data"
                novalidate>

            <input type="hidden" name="_csrf"
                   value="${sessionScope.csrfToken}">
            <input type="hidden" name="propertyId"
                   value="${property.propertyId}">

            <!-- Basic Info -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(26,60,94,0.04);
                        border:1px solid rgba(26,60,94,0.1)">
              <h5 class="mb-3" style="color:var(--re-primary)">
                <i class="bi bi-info-circle me-2"></i>Basic Information
              </h5>
              <div class="re-form-group">
                <label>Property Title <span class="required">*</span></label>
                <input type="text" class="re-form-control" name="title"
                       value="<c:out value='${property.title}'/>"
                       required minlength="10">
              </div>
              <div class="row g-3">
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Property Type <span class="required">*</span></label>
                    <select class="re-form-control" name="typeId" required>
                      <c:forEach var="type" items="${propertyTypes}">
                        <option value="${type.typeId}"
                          <c:if test="${property.typeId == type.typeId}">
                            selected
                          </c:if>>
                          <c:out value="${type.typeName}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Category <span class="required">*</span></label>
                    <select class="re-form-control" name="categoryId" required>
                      <c:forEach var="cat" items="${propertyCategories}">
                        <option value="${cat.categoryId}"
                          <c:if test="${property.categoryId == cat.categoryId}">
                            selected
                          </c:if>>
                          <c:out value="${cat.categoryName}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Annual Rent (₦) <span class="required">*</span></label>
                    <input type="number" class="re-form-control"
                           name="price" min="1000" step="500"
                           value="${property.price}" required>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Bedrooms</label>
                    <input type="number" class="re-form-control"
                           name="bedrooms" min="0" max="20"
                           value="${property.bedrooms}">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Bathrooms</label>
                    <input type="number" class="re-form-control"
                           name="bathrooms" min="0" max="20"
                           value="${property.bathrooms}">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Furnishing Status</label>
                    <select class="re-form-control" name="furnishingStatus">
                      <option value="UNFURNISHED"
                        <c:if test="${property.furnishingStatus == 'UNFURNISHED'}">selected</c:if>>
                        Unfurnished
                      </option>
                      <option value="SEMI_FURNISHED"
                        <c:if test="${property.furnishingStatus == 'SEMI_FURNISHED'}">selected</c:if>>
                        Semi-Furnished
                      </option>
                      <option value="FURNISHED"
                        <c:if test="${property.furnishingStatus == 'FURNISHED'}">selected</c:if>>
                        Fully Furnished
                      </option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="re-form-group">
                <label>Description <span class="required">*</span></label>
                <textarea class="re-form-control" name="description"
                          rows="5" required minlength="30">
                  <c:out value="${property.description}"/>
                </textarea>
              </div>
            </div>

            <!-- Campus Distance -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(13,202,240,0.04);
                        border:1px solid rgba(13,202,240,0.15)">
              <h5 class="mb-3" style="color:#055160">
                <i class="bi bi-mortarboard me-2"></i>
                Campus Distance &amp; Quality
              </h5>
              <div class="re-form-group">
                <label>Distance from AOPE
                  <span class="required">*</span>
                </label>
                <div class="row g-2 mt-1">
                  <c:forEach var="dist"
                             items="ON_CAMPUS,LESS_5MIN,5_TO_10MIN,10_TO_15MIN,ABOVE_15MIN"
                             varStatus="st">
                    <div class="col">
                      <input type="radio" class="btn-check"
                             name="campusDistance"
                             id="edit_dist_${st.index}"
                             value="${dist}"
                             <c:if test="${property.campusDistance == dist}">checked</c:if>
                             required>
                      <label class="btn btn-outline-secondary w-100 py-2"
                             for="edit_dist_${st.index}"
                             style="font-size:0.78rem">
                        <c:out value="${dist}"/>
                      </label>
                    </div>
                  </c:forEach>
                </div>
              </div>
              <div class="row g-3 mt-1">
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Water Availability</label>
                    <select class="re-form-control" name="waterAvailability">
                      <c:forEach var="w"
                                 items="UNKNOWN,EXCELLENT,GOOD,FAIR,POOR,NONE">
                        <option value="${w}"
                          <c:if test="${property.waterAvailability == w}">selected</c:if>>
                          <c:out value="${w}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Electricity</label>
                    <select class="re-form-control" name="electricity">
                      <c:forEach var="e"
                                 items="UNKNOWN,24HRS,PARTIAL,RARE,NONE">
                        <option value="${e}"
                          <c:if test="${property.electricity == e}">selected</c:if>>
                          <c:out value="${e}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Security Level</label>
                    <select class="re-form-control" name="securityLevel">
                      <c:forEach var="s" items="UNKNOWN,HIGH,MEDIUM,LOW">
                        <option value="${s}"
                          <c:if test="${property.securityLevel == s}">selected</c:if>>
                          <c:out value="${s}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Road Accessibility</label>
                    <select class="re-form-control" name="roadAccessibility">
                      <c:forEach var="r"
                                 items="UNKNOWN,TARRED,MOTORABLE,ROUGH">
                        <option value="${r}"
                          <c:if test="${property.roadAccessibility == r}">selected</c:if>>
                          <c:out value="${r}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Internet Available?</label>
                    <div class="form-check form-switch mt-2">
                      <input class="form-check-input" type="checkbox"
                             role="switch" id="editInternet"
                             name="internetAvailable"
                             ${property.internetAvailable ? 'checked' : ''}>
                      <label class="form-check-label" for="editInternet">
                        Wi-Fi / Fibre Available
                      </label>
                    </div>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Compound Fenced?</label>
                    <div class="form-check form-switch mt-2">
                      <input class="form-check-input" type="checkbox"
                             role="switch" id="editFenced"
                             name="isFenced"
                             ${property.fenced ? 'checked' : ''}>
                      <label class="form-check-label" for="editFenced">
                        Fenced Compound
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Amenities -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(25,135,84,0.04);
                        border:1px solid rgba(25,135,84,0.15)">
              <h5 class="mb-3" style="color:#0a5c37">
                <i class="bi bi-check2-square me-2"></i>Amenities
              </h5>
              <div class="row g-2">
                <c:forEach var="amenity" items="${amenities}">
                  <c:set var="isChecked" value="false"/>
                  <c:forEach var="pa" items="${property.amenities}">
                    <c:if test="${pa.amenityId == amenity.amenityId}">
                      <c:set var="isChecked" value="true"/>
                    </c:if>
                  </c:forEach>
                  <div class="col-md-4 col-6">
                    <div class="form-check"
                         style="background:rgba(25,135,84,0.05);
                                padding:8px 12px;border-radius:8px;
                                border:1px solid rgba(25,135,84,0.1)">
                      <input class="form-check-input" type="checkbox"
                             name="amenities"
                             value="${amenity.amenityId}"
                             id="edit_a_${amenity.amenityId}"
                             ${isChecked ? 'checked' : ''}>
                      <label class="form-check-label"
                             for="edit_a_${amenity.amenityId}"
                             style="font-size:0.82rem;cursor:pointer">
                        <i class="${amenity.amenityIcon} me-1"
                           style="color:#198754"></i>
                        <c:out value="${amenity.amenityName}"/>
                      </label>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </div>

            <div class="d-flex gap-3 justify-content-end">
              <a href="<%= ctx %>/landlord/manage-properties.jsp"
                 class="btn btn-outline-secondary">Cancel</a>
              <button type="submit" class="btn btn-primary-brand btn-lg-custom">
                <i class="bi bi-check-circle me-2"></i>Save Changes
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>