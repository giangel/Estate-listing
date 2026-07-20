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

  <!-- Breadcrumb -->
  <nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb" style="font-size:0.82rem">
      <li class="breadcrumb-item">
        <a href="<%= ctx %>/landlord/landlord-dashboard.jsp">Dashboard</a>
      </li>
      <li class="breadcrumb-item active">Add New Property</li>
    </ol>
  </nav>

  <div class="row justify-content-center">
    <div class="col-lg-10">

      <div class="card border-0 shadow-sm rounded-4">
        <div class="card-header border-0 pt-4 pb-0 px-4 bg-white">
          <h3 class="mb-0">
            <i class="bi bi-plus-circle me-2"
               style="color:var(--re-secondary)"></i>
            List a New Property
          </h3>
          <p class="text-muted mt-1 mb-0" style="font-size:0.875rem">
            Fill in accurate details. Your listing will go live after admin approval.
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

          <form action="<%= ctx %>/landlord/create-property"
                method="POST"
                enctype="multipart/form-data"
                novalidate>

            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

            <!-- ================================================
                 SECTION 1: BASIC INFORMATION
                 ================================================ -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(26,60,94,0.04);
                        border:1px solid rgba(26,60,94,0.1)">
              <h5 class="mb-3" style="color:var(--re-primary)">
                <i class="bi bi-info-circle me-2"></i>Basic Information
              </h5>

              <div class="re-form-group">
                <label>Property Title
                  <span class="required">*</span>
                </label>
                <input type="text" class="re-form-control"
                       name="title" maxlength="255"
                       placeholder="e.g. Modern Self-Contain Near AOP Gate"
                       required minlength="10">
                <div style="font-size:0.75rem;color:var(--re-gray-500);margin-top:4px">
                  Minimum 10 characters. Be descriptive and accurate.
                </div>
              </div>

              <div class="row g-3">
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Property Type <span class="required">*</span></label>
                    <select class="re-form-control" name="typeId" required>
                      <option value="">- Select Type -</option>
                      <c:forEach var="type" items="${propertyTypes}">
                        <option value="${type.typeId}">
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
                      <option value="">- Select Category -</option>
                      <c:forEach var="cat" items="${propertyCategories}">
                        <option value="${cat.categoryId}">
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
                           placeholder="85000" required>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Bedrooms</label>
                    <input type="number" class="re-form-control"
                           name="bedrooms" min="0" max="20" value="1">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Bathrooms</label>
                    <input type="number" class="re-form-control"
                           name="bathrooms" min="0" max="20" value="1">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Size (square metres)</label>
                    <input type="number" class="re-form-control"
                           name="sizeSqm" min="0" step="0.5"
                           placeholder="25.5">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Furnishing Status</label>
                    <select class="re-form-control" name="furnishingStatus">
                      <option value="UNFURNISHED">Unfurnished</option>
                      <option value="SEMI_FURNISHED">Semi-Furnished</option>
                      <option value="FURNISHED">Fully Furnished</option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="re-form-group">
                <label>Full Description <span class="required">*</span></label>
                <textarea class="re-form-control" name="description"
                          rows="5" minlength="30" required
                          placeholder="Describe the property in detail. Include key features, nearby facilities, and any important terms..."></textarea>
                <div style="font-size:0.75rem;color:var(--re-gray-500);margin-top:4px">
                  Minimum 30 characters. Be honest and detailed.
                </div>
              </div>
            </div>

            <!-- ================================================
                 SECTION 2: CAMPUS INTELLIGENCE
                 ================================================ -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(13,202,240,0.04);
                        border:1px solid rgba(13,202,240,0.15)">
              <h5 class="mb-3" style="color:#055160">
                <i class="bi bi-mortarboard me-2"></i>
                Campus Distance &amp; Quality Indicators
              </h5>

              <div class="re-form-group">
                <label>Distance from AOPE Campus
                  <span class="required">*</span>
                </label>
                <div class="row g-2 mt-1">
                  <div class="col-md col-6">
                    <input type="radio" class="btn-check"
                           name="campusDistance" id="dist_on"
                           value="ON_CAMPUS" required>
                    <label class="btn btn-outline-secondary w-100 py-2"
                           for="dist_on" style="font-size:0.82rem">
                      <i class="bi bi-building-fill d-block mb-1"
                         style="color:#198754;font-size:1.1rem"></i>
                      On Campus
                    </label>
                  </div>
                  <div class="col-md col-6">
                    <input type="radio" class="btn-check"
                           name="campusDistance" id="dist_5"
                           value="LESS_5MIN">
                    <label class="btn btn-outline-secondary w-100 py-2"
                           for="dist_5" style="font-size:0.82rem">
                      <i class="bi bi-person-walking d-block mb-1"
                         style="color:#0dcaf0;font-size:1.1rem"></i>
                      &lt; 5 min
                    </label>
                  </div>
                  <div class="col-md col-6">
                    <input type="radio" class="btn-check"
                           name="campusDistance" id="dist_5_10"
                           value="5_TO_10MIN">
                    <label class="btn btn-outline-secondary w-100 py-2"
                           for="dist_5_10" style="font-size:0.82rem">
                      <i class="bi bi-person-walking d-block mb-1"
                         style="color:#ffc107;font-size:1.1rem"></i>
                      5–10 min
                    </label>
                  </div>
                  <div class="col-md col-6">
                    <input type="radio" class="btn-check"
                           name="campusDistance" id="dist_10_15"
                           value="10_TO_15MIN">
                    <label class="btn btn-outline-secondary w-100 py-2"
                           for="dist_10_15" style="font-size:0.82rem">
                      <i class="bi bi-bicycle d-block mb-1"
                         style="color:#fd7e14;font-size:1.1rem"></i>
                      10–15 min
                    </label>
                  </div>
                  <div class="col-md col-6">
                    <input type="radio" class="btn-check"
                           name="campusDistance" id="dist_15"
                           value="ABOVE_15MIN">
                    <label class="btn btn-outline-secondary w-100 py-2"
                           for="dist_15" style="font-size:0.82rem">
                      <i class="bi bi-car-front d-block mb-1"
                         style="color:#dc3545;font-size:1.1rem"></i>
                      &gt; 15 min
                    </label>
                  </div>
                </div>
              </div>

              <div class="row g-3 mt-2">
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Water Availability</label>
                    <select class="re-form-control" name="waterAvailability">
                      <option value="UNKNOWN">Unknown</option>
                      <option value="EXCELLENT">Excellent (Borehole 24/7)</option>
                      <option value="GOOD">Good (Reliable Supply)</option>
                      <option value="FAIR">Fair (Occasional Outages)</option>
                      <option value="POOR">Poor (Infrequent)</option>
                      <option value="NONE">None (No Water Supply)</option>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Electricity Supply</label>
                    <select class="re-form-control" name="electricity">
                      <option value="UNKNOWN">Unknown</option>
                      <option value="24HRS">24 Hours (Generator Backup)</option>
                      <option value="PARTIAL">Partial (12+ hrs/day)</option>
                      <option value="RARE">Rare (Few hours/day)</option>
                      <option value="NONE">None</option>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Security Level</label>
                    <select class="re-form-control" name="securityLevel">
                      <option value="UNKNOWN">Unknown</option>
                      <option value="HIGH">High (CCTV + Guard)</option>
                      <option value="MEDIUM">Medium (Watchman)</option>
                      <option value="LOW">Low (No Security)</option>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Road Accessibility</label>
                    <select class="re-form-control" name="roadAccessibility">
                      <option value="UNKNOWN">Unknown</option>
                      <option value="TARRED">Tarred Road</option>
                      <option value="MOTORABLE">Motorable (Rough Tarred)</option>
                      <option value="ROUGH">Rough / Untarred</option>
                    </select>
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label>Internet Available?</label>
                    <div class="form-check form-switch mt-2">
                      <input class="form-check-input" type="checkbox"
                             role="switch" id="internetAvailable"
                             name="internetAvailable">
                      <label class="form-check-label" for="internetAvailable">
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
                             role="switch" id="isFenced" name="isFenced">
                      <label class="form-check-label" for="isFenced">
                        Fully / Partially Fenced
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- ================================================
                 SECTION 3: LOCATION
                 ================================================ -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(232,160,32,0.04);
                        border:1px solid rgba(232,160,32,0.15)">
              <h5 class="mb-3" style="color:#7a4b00">
                <i class="bi bi-geo-alt me-2"></i>Property Location
              </h5>
              <div class="row g-3">
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Area/Neighbourhood
                      <span class="required">*</span>
                    </label>
                    <input type="text" class="re-form-control"
                           name="areaName" required
                           placeholder="e.g. Eruwa Town Centre, New Layout">
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="re-form-group">
                    <label>Street Address</label>
                    <input type="text" class="re-form-control"
                           name="streetAddress"
                           placeholder="e.g. 12 AOP Road, Eruwa">
                  </div>
                </div>
                <div class="col-12">
                  <div class="re-form-group">
                    <label>Nearest Landmark</label>
                    <input type="text" class="re-form-control"
                           name="landmark"
                           placeholder="e.g. Behind First Bank Eruwa">
                  </div>
                </div>
              </div>
            </div>

            <!-- ================================================
                 SECTION 4: AMENITIES
                 ================================================ -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(25,135,84,0.04);
                        border:1px solid rgba(25,135,84,0.15)">
              <h5 class="mb-3" style="color:#0a5c37">
                <i class="bi bi-check2-square me-2"></i>
                Amenities &amp; Facilities
              </h5>
              <p style="font-size:0.82rem;color:var(--re-gray-500)">
                Select all amenities available in this property.
              </p>
              <div class="row g-2">
                <c:forEach var="amenity" items="${amenities}">
                  <div class="col-md-4 col-6">
                    <div class="form-check"
                         style="background:rgba(25,135,84,0.05);
                                padding:8px 12px;border-radius:8px;
                                border:1px solid rgba(25,135,84,0.1)">
                      <input class="form-check-input" type="checkbox"
                             name="amenities"
                             value="${amenity.amenityId}"
                             id="amenity_${amenity.amenityId}">
                      <label class="form-check-label"
                             for="amenity_${amenity.amenityId}"
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

            <!-- ================================================
                 SECTION 5: IMAGE UPLOAD
                 ================================================ -->
            <div class="p-3 mb-4 rounded-3"
                 style="background:rgba(13,110,253,0.04);
                        border:1px solid rgba(13,110,253,0.15)">
              <h5 class="mb-3" style="color:#084298">
                <i class="bi bi-images me-2"></i>
                Property Images
              </h5>
              <p style="font-size:0.82rem;color:var(--re-gray-500)">
                Upload up to 10 images. Accepted formats: JPEG, PNG, WebP.
                Maximum 5 MB per image. The first image selected will be the
                cover image by default.
              </p>

              <div class="border-2 border-dashed rounded-3 p-4 text-center"
                   style="border:2px dashed rgba(13,110,253,0.3);
                          background:rgba(13,110,253,0.02);cursor:pointer"
                   onclick="document.getElementById('propertyImages').click()">
                <i class="bi bi-cloud-upload"
                   style="font-size:2.5rem;color:rgba(13,110,253,0.4)"></i>
                <p class="mb-1 mt-2 fw-semibold">
                  Click to upload or drag and drop
                </p>
                <p class="text-muted" style="font-size:0.8rem">
                  JPEG, PNG, WebP - Max 5 MB each - Up to 10 images
                </p>
              </div>

              <input type="file" id="propertyImages" name="images"
                     accept="image/jpeg,image/png,image/webp"
                     multiple style="display:none"
                     onchange="previewImages(this)">

              <!-- Image Preview Grid -->
              <div class="row g-2 mt-3" id="imagePreviewGrid"></div>

              <!-- Cover Image Selector -->
              <input type="hidden" name="coverImageIndex"
                     id="coverImageIndex" value="0">
            </div>

            <!-- ================================================
                 SUBMIT BUTTONS
                 ================================================ -->
            <div class="d-flex gap-3 justify-content-end">
              <a href="<%= ctx %>/landlord/manage-properties.jsp"
                 class="btn btn-outline-secondary">
                Cancel
              </a>
              <button type="submit" class="btn btn-primary-brand btn-lg-custom">
                <i class="bi bi-cloud-upload me-2"></i>
                Submit for Approval
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
<script>
// Image preview and cover selection
function previewImages(input) {
  var grid  = document.getElementById('imagePreviewGrid');
  var files = input.files;
  grid.innerHTML = '';

  if (files.length > 10) {
    alert('Maximum 10 images allowed. Only the first 10 will be uploaded.');
  }

  var limit = Math.min(files.length, 10);
  for (var i = 0; i < limit; i++) {
    (function(index) {
      var reader = new FileReader();
      reader.onload = function(e) {
        var col = document.createElement('div');
        col.className = 'col-md-3 col-6';
        col.innerHTML =
          '<div style="position:relative;border-radius:10px;overflow:hidden;' +
          'border:2px solid ' +
          (index === 0 ? 'var(--re-secondary)' : 'var(--re-gray-200)') + '">' +
          '<img src="' + e.target.result + '" ' +
          'style="width:100%;height:120px;object-fit:cover">' +
          '<div style="position:absolute;top:4px;right:4px">' +
          '<button type="button" onclick="setCover(' + index + ')" ' +
          'style="background:' +
          (index === 0 ? 'var(--re-secondary)' : 'rgba(0,0,0,0.5)') +
          ';color:white;border:none;border-radius:50%;' +
          'width:28px;height:28px;font-size:0.7rem;cursor:pointer" ' +
          'id="coverBtn' + index + '" title="Set as cover">' +
          '<i class="bi bi-star-fill"></i></button></div>' +
          (index === 0 ? '<div style="position:absolute;bottom:0;left:0;right:0;' +
          'background:var(--re-secondary);color:white;text-align:center;' +
          'font-size:0.7rem;padding:2px">Cover</div>' : '') +
          '</div>';
        grid.appendChild(col);
      };
      reader.readAsDataURL(files[index]);
    })(i);
  }
}

function setCover(index) {
  document.getElementById('coverImageIndex').value = index;
  // Update visual indicator
  document.querySelectorAll('[id^="coverBtn"]').forEach(function(btn, i) {
    btn.style.background = (i === index)
      ? 'var(--re-secondary)' : 'rgba(0,0,0,0.5)';
  });
}
</script>
</body>
</html>