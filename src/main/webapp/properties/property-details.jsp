<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% 
    String ctx = request.getContextPath();
    boolean loggedIn = session.getAttribute("loggedInUser") != null; 
    
    // Calculate safe dynamic min date boundary for appointment booker
    String todayStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    request.setAttribute("todayDate", todayStr);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <!-- Swiper CSS Embedded Dependency directly in header context -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css">
</head>
<body data-ctx="<%= ctx %>">
<jsp:include page="/includes/navbar.jsp" />

<div class="container py-4">

  <!-- Breadcrumb Navigation Bar -->
  <nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb" style="font-size:0.82rem">
      <li class="breadcrumb-item"><a href="<%= ctx %>/">Home</a></li>
      <li class="breadcrumb-item"><a href="<%= ctx %>/properties">Properties</a></li>
      <li class="breadcrumb-item active" aria-current="page">
        <c:out value="${property.title}"/>
      </li>
    </ol>
  </nav>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="row g-4">

    <!-- ============================================================
         LEFT COLUMN - Gallery + Comprehensive Tab Details
         ============================================================ -->
    <div class="col-lg-8">

      <!-- Image Swiper Core Slider -->
      <div class="mb-4">
        <c:choose>
          <c:when test="${not empty property.images}">
            <div class="swiper propertySwiper mb-3" style="border-radius:var(--re-border-radius); overflow:hidden; max-height:420px">
              <div class="swiper-wrapper">
                <c:forEach var="img" items="${property.images}">
                  <div class="swiper-slide">
                    <img src="<%= ctx %>/${img.imagePath}"
                         alt="<c:out value='${not empty img.imageCaption ? img.imageCaption : property.title}'/>"
                         style="width:100%;height:420px;object-fit:cover">
                  </div>
                </c:forEach>
              </div>
              <div class="swiper-pagination"></div>
              <div class="swiper-button-prev"></div>
              <div class="swiper-button-next"></div>
            </div>
            
            <!-- Interactive Gallery Navigation Thumbnails -->
            <div class="d-flex gap-2 overflow-auto pb-2">
              <c:forEach var="img" items="${property.images}" varStatus="st">
                <img src="${ctx}/${img.imagePath}"
                     alt="Thumbnail ${st.index + 1}"
                     style="width:80px;height:60px;object-fit:cover; border-radius:8px; cursor:pointer; flex-shrink:0; border:2px solid ${st.first ? 'var(--re-primary)' : 'transparent'}"
                     onclick="swiper.slideTo(${st.index})"
                     class="property-thumb">
              </c:forEach>
            </div>
          </c:when>
          <c:otherwise>
            <div class="w-100 placeholder-container" style="border-radius:var(--re-border-radius); overflow:hidden;">
              <img src="${ctx}/assets/images/placeholder.jpg" alt="No Images Available" style="width:100%; height:380px; object-fit:cover;">
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- General Header Content Module -->
      <div class="d-flex align-items-start justify-content-between flex-wrap gap-3 mb-3">
        <div>
          <h1 style="font-size:1.6rem; margin-bottom:0.5rem">
            <c:out value="${property.title}"/>
          </h1>
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <span class="re-badge <c:out value='${property.statusBadgeClass}' default='bg-primary'/>">
              <c:out value="${property.propertyStatus}"/>
            </span>
            <c:if test="${property.verified}">
              <span class="re-badge re-badge-verified">
                <i class="bi bi-patch-check-fill"></i> Verified Property
              </span>
            </c:if>
            <c:if test="${property.featured}">
              <span class="re-badge re-badge-featured">
                <i class="bi bi-star-fill"></i> Featured
              </span>
            </c:if>
            <span class="re-badge re-badge-distance">
              <i class="<c:out value='${property.campusDistanceIcon}' default='bi-building'/>"></i>
              <c:out value="${property.campusDistanceLabel}"/>
            </span>
          </div>
        </div>

        <!-- Meta Interaction Triggers -->
        <div class="d-flex gap-2">
          <button class="btn btn-outline-brand btn-sm-custom ${isSaved ? 'btn-secondary-brand' : ''}"
                  id="saveBtn"
                  onclick="toggleWishlistDetail(this, <c:out value='${property.propertyId}'/>)">
            <i class="bi ${isSaved ? 'bi-heart-fill' : 'bi-heart'} me-1"></i>
            <span>${isSaved ? 'Saved' : 'Save'}</span>
          </button>
          <button class="btn btn-outline-brand btn-sm-custom"
                  onclick="CompareManager.add(<c:out value='${property.propertyId}'/>, '<c:out value='${property.title}'/>')">
            <i class="bi bi-bar-chart-steps me-1"></i> Compare
          </button>
        </div>
      </div>

      <!-- Location Structural Identity -->
      <c:if test="${not empty property.location}">
        <div class="d-flex align-items-center gap-2 text-muted mb-4">
          <i class="bi bi-geo-alt-fill" style="color:var(--re-secondary)"></i>
          <span>
            <c:out value="${property.location.streetAddress}"/>,
            <c:out value="${property.location.areaName}"/>,
            <c:out value="${property.location.lga}"/>
            <c:if test="${not empty property.location.landmark}">
              - Near: <c:out value="${property.location.landmark}"/>
            </c:if>
          </span>
        </div>
      </c:if>

      <!-- Navigation Detail Selection Tabs -->
      <ul class="nav nav-tabs mb-4" id="propTabs" role="tablist">
        <li class="nav-item" role="presentation">
          <button class="nav-link active" id="details-tab" data-bs-toggle="tab" data-bs-target="#tabDetails" type="button" role="tab">Details</button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="amenities-tab" data-bs-toggle="tab" data-bs-target="#tabAmenities" type="button" role="tab">Amenities</button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="quality-tab" data-bs-toggle="tab" data-bs-target="#tabQuality" type="button" role="tab">Quality Metrics</button>
        </li>
        <li class="nav-item" role="presentation">
          <button class="nav-link" id="reviews-tab" data-bs-toggle="tab" data-bs-target="#tabReviews" type="button" role="tab">
            Reviews (<c:out value="${fn:length(reviews)}"/>)
          </button>
        </li>
      </ul>

      <div class="tab-content" id="propTabsContent">

        <!-- DETAILS TAB WRAPPER -->
        <div class="tab-pane fade show active" id="tabDetails" role="tabpanel" aria-labelledby="details-tab">
          <div class="row g-3 mb-4">
            <div class="col-6 col-md-3">
              <div class="p-3 text-center rounded-3 bg-light">
                <i class="bi bi-house-door" style="font-size:1.5rem; color:var(--re-primary)"></i>
                <div class="fw-bold mt-1"><c:out value="${property.typeName}"/></div>
                <div style="font-size:0.75rem; color:var(--re-gray-500)">Property Type</div>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="p-3 text-center rounded-3 bg-light">
                <i class="bi bi-door-open" style="font-size:1.5rem; color:var(--re-primary)"></i>
                <div class="fw-bold mt-1"><c:out value="${property.bedrooms}"/> Bedroom(s)</div>
                <div style="font-size:0.75rem; color:var(--re-gray-500)">Space Allocation</div>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="p-3 text-center rounded-3 bg-light">
                <i class="bi bi-water" style="font-size:1.5rem; color:var(--re-primary)"></i>
                <div class="fw-bold mt-1"><c:out value="${property.bathrooms}"/> Bathroom(s)</div>
                <div style="font-size:0.75rem; color:var(--re-gray-500)">Toilets/Baths</div>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="p-3 text-center rounded-3 bg-light">
                <i class="bi bi-rulers" style="font-size:1.5rem; color:var(--re-primary)"></i>
                <div class="fw-bold mt-1">
                  <c:choose>
                    <c:when test="${not empty property.sizeSqm}"><c:out value="${property.sizeSqm}"/> m²</c:when>
                    <c:otherwise>N/A</c:otherwise>
                  </c:choose>
                </div>
                <div style="font-size:0.75rem; color:var(--re-gray-500)">Total Area</div>
              </div>
            </div>
          </div>

          <h5 class="mb-3">Description</h5>
          <p style="line-height:1.8; color:var(--re-gray-700)">
            <c:out value="${property.description}"/>
          </p>

          <!-- Additional Metadata Properties Layout -->
          <div class="row g-2 mt-3">
            <div class="col-md-6">
              <div class="d-flex align-items-center gap-2 p-2 rounded bg-light">
                <i class="bi bi-card-checklist" style="color:var(--re-primary)"></i>
                <span style="font-size:0.85rem"><strong>Category:</strong> <c:out value="${property.categoryName}"/></span>
              </div>
            </div>
            <div class="col-md-6">
              <div class="d-flex align-items-center gap-2 p-2 rounded bg-light">
                <i class="bi bi-sofa" style="color:var(--re-primary)"></i>
                <span style="font-size:0.85rem"><strong>Furnishing Status:</strong> <c:out value="${property.furnishingStatus}"/></span>
              </div>
            </div>
            <div class="col-md-6">
              <div class="d-flex align-items-center gap-2 p-2 rounded bg-light">
                <i class="bi bi-eye" style="color:var(--re-primary)"></i>
                <span style="font-size:0.85rem"><strong>Total Views:</strong> <c:out value="${property.viewCount}"/></span>
              </div>
            </div>
            <div class="col-md-6">
              <div class="d-flex align-items-center gap-2 p-2 rounded bg-light">
                <i class="bi bi-heart" style="color:var(--re-danger)"></i>
                <span style="font-size:0.85rem"><strong>Wishlist Saves:</strong> <c:out value="${property.saveCount}"/></span>
              </div>
            </div>
          </div>
        </div>

        <!-- AMENITIES DATA VIEW TAB -->
        <div class="tab-pane fade" id="tabAmenities" role="tabpanel" aria-labelledby="amenities-tab">
          <c:choose>
            <c:when test="${not empty property.amenities}">
              <div class="row g-3">
                <c:forEach var="amenity" items="${property.amenities}">
                  <div class="col-md-4 col-6">
                    <div class="d-flex align-items-center gap-2 p-3 rounded-3" style="background:rgba(25,135,84,0.06); border:1px solid rgba(25,135,84,0.15)">
                      <i class="<c:out value='${amenity.amenityIcon}' default='bi-check-circle'/>" style="color:#198754; font-size:1.2rem"></i>
                      <span style="font-size:0.875rem; font-weight:500">
                        <c:out value="${amenity.amenityName}"/>
                      </span>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <p class="text-muted">No custom operational amenities listed for this property entry.</p>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- QUALITY ANALYSIS GRID TAB -->
        <div class="tab-pane fade" id="tabQuality" role="tabpanel" aria-labelledby="quality-tab">
          <div class="row g-3">
            <div class="col-md-6">
              <div class="quality-badge <c:out value='${property.waterQualityClass}' default='quality-fair'/> w-100">
                <i class="bi bi-droplet-fill"></i>
                <span class="quality-label">Water Availability:</span>
                <strong><c:out value="${property.waterAvailability}"/></strong>
              </div>
            </div>
            <div class="col-md-6">
              <div class="quality-badge <c:out value='${property.electricityQualityClass}' default='quality-fair'/> w-100">
                <i class="bi bi-lightning-charge-fill"></i>
                <span class="quality-label">Electricity Index:</span>
                <strong><c:out value="${property.electricity}"/></strong>
              </div>
            </div>
            <div class="col-md-6">
              <div class="quality-badge <c:out value='${property.securityQualityClass}' default='quality-fair'/> w-100">
                <i class="bi bi-shield-fill-check"></i>
                <span class="quality-label">Security Parameters:</span>
                <strong><c:out value="${property.securityLevel}"/></strong>
              </div>
            </div>
            <div class="col-md-6">
              <div class="quality-badge <c:out value='${property.roadQualityClass}' default='quality-fair'/> w-100">
                <i class="bi bi-signpost-fill"></i>
                <span class="quality-label">Road Infrastructure:</span>
                <strong><c:out value="${property.roadAccessibility}"/></strong>
              </div>
            </div>
            <div class="col-md-6">
              <div class="quality-badge ${property.internetAvailable ? 'quality-excellent' : 'quality-none'} w-100">
                <i class="bi bi-wifi"></i>
                <span class="quality-label">Network / Internet:</span>
                <strong>${property.internetAvailable ? 'Available' : 'Not Available'}</strong>
              </div>
            </div>
            <div class="col-md-6">
              <div class="quality-badge ${property.fenced ? 'quality-excellent' : 'quality-fair'} w-100">
                <i class="bi bi-bricks"></i>
                <span class="quality-label">Fencing Shell Perimeter:</span>
                <strong>${property.fenced ? 'Fully Fenced' : 'Not Fenced'}</strong>
              </div>
            </div>
          </div>
        </div>

        <!-- REVIEWS LOG MODULE TAB -->
        <div class="tab-pane fade" id="tabReviews" role="tabpanel" aria-labelledby="reviews-tab">
          <c:choose>
            <c:when test="${not empty reviews}">
              <c:forEach var="review" items="${reviews}">
                <div class="card border-0 bg-light rounded-3 mb-3 p-3">
                  <div class="d-flex align-items-center gap-2 mb-2">
                    <div style="width:40px;height:40px;border-radius:50%; background:var(--re-primary); display:flex; align-items:center; justify-content:center; color:white; font-weight:700">
                      <c:out value="${not empty review.reviewerName ? fn:toUpperCase(fn:substring(review.reviewerName, 0, 1)) : 'U'}"/>
                    </div>
                    <div>
                      <div style="font-weight:600; font-size:0.9rem">
                        <c:out value="${review.reviewerName}"/>
                      </div>
                      <div style="font-size:0.75rem; color:var(--re-gray-500)">
                        <fmt:formatDate value="${review.createdAt}" pattern="dd MMM yyyy"/>
                      </div>
                    </div>
                    <c:if test="${review.ratingValue > 0}">
                      <div class="ms-auto star-rating">
                        <c:forEach begin="1" end="${review.ratingValue}">
                          <i class="bi bi-star-fill" style="color:#f5a623"></i>
                        </c:forEach>
                      </div>
                    </c:if>
                  </div>
                  <h6 style="font-size:0.9rem; margin-bottom:4px">
                    <c:out value="${review.reviewTitle}"/>
                  </h6>
                  <p style="font-size:0.85rem; color:var(--re-gray-700); margin:0">
                    <c:out value="${review.reviewBody}"/>
                  </p>
                </div>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <div class="text-center py-4 text-muted">
                <i class="bi bi-chat-left-text" style="font-size:2rem; opacity:0.4"></i>
                <p class="mt-2">No verification reviews yet posted. Be the first to add an evaluation entry!</p>
              </div>
            </c:otherwise>
          </c:choose>

          <!-- Authenticated Users Review Entry Framework -->
          <% if (loggedIn) { %>
          <div class="mt-4 p-3 rounded-3" style="background:rgba(26,60,94,0.04); border:1px solid var(--re-gray-200)">
            <h6 class="fw-bold mb-3">Post verified resident review</h6>
            <form action="<%= ctx %>/user/review" method="POST">
              <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
              <input type="hidden" name="propertyId" value="<c:out value='${property.propertyId}'/>">

              <div class="mb-3">
                <label class="form-label d-block mb-1" style="font-size:0.85rem; font-weight:600">Your Rating</label>
                <div class="d-flex gap-2" id="starRatingWidget">
                  <c:forEach begin="1" end="5" var="s">
                    <i class="bi bi-star fs-4" style="cursor:pointer; color:var(--re-gray-300)" data-value="${s}" onclick="setRating(${s})" onmouseover="highlightStars(${s})" onmouseout="resetStars()"></i>
                  </c:forEach>
                </div>
                <input type="hidden" name="rating" id="ratingInput" value="<c:out value='${userRating}' default='0'/>">
              </div>

              <div class="re-form-group mb-3">
                <label class="form-label" style="font-size:0.85rem; font-weight:600">Headline Title</label>
                <input type="text" class="re-form-control" name="reviewTitle" placeholder="Brief headline for your review context">
              </div>
              <div class="re-form-group mb-3">
                <label class="form-label" style="font-size:0.85rem; font-weight:600">Review Message Content</label>
                <textarea class="re-form-control" name="reviewBody" rows="4" placeholder="Share your spatial experience with this structural environment..." style="resize:vertical"></textarea>
              </div>
              <button type="submit" class="btn btn-primary-brand">
                <i class="bi bi-send me-1"></i> Post Entry
              </button>
            </form>
          </div>
          <% } else { %>
          <div class="text-center py-3">
            <a href="<%= ctx %>/login" class="btn btn-outline-brand">
              <i class="bi bi-box-arrow-in-right me-1"></i> Login to add evaluation
            </a>
          </div>
          <% } %>
        </div>

      </div>

      <!-- Compliance Reporting / Integrity Guardrail Module -->
      <% if (loggedIn) { %>
      <div class="mt-4 p-3 rounded-3" style="background:rgba(220,53,69,0.04); border:1px solid rgba(220,53,69,0.15)">
        <h6 class="text-danger mb-3"><i class="bi bi-shield-exclamation me-1"></i> Report Suspicious Activity / Fraud Alert</h6>
        <form action="<%= ctx %>/admin/fraud" method="POST" onsubmit="return confirm('Submit official alert confirmation status parameter to administration logs?')">
         <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
          <input type="hidden" name="action" value="report">
          <input type="hidden" name="propertyId" value="<c:out value='${property.propertyId}'/>">
          <div class="row g-2">
            <div class="col-md-4">
              <select class="re-form-control" name="reason" style="height:38px; font-size:0.82rem" required>
                <option value="">Select reason</option>
                <option value="DUPLICATE">Duplicate verification listing</option>
                <option value="FAKE">Fake/non-existent structural target</option>
                <option value="OVERPRICED">Grossly non-compliant threshold cost</option>
                <option value="OCCUPIED">Already occupied or unallocated status</option>
                <option value="OTHER">Other baseline issue</option>
              </select>
            </div>
            <div class="col-md-6">
              <input type="text" class="re-form-control" name="details" placeholder="Describe context variance parameters (min 20 chars)" style="height:38px; font-size:0.82rem" required>
            </div>
            <div class="col-md-2">
              <button type="submit" class="btn w-100" style="height:38px; background:var(--re-danger); color:white; font-size:0.82rem">Report</button>
            </div>
          </div>
        </form>
      </div>
      <% } %>

    </div>

    <!-- ============================================================
         RIGHT COLUMN - Dynamic Commercial Context Controls
         ============================================================ -->
    <div class="col-lg-4">
      <div class="contact-card p-4 border rounded-4 shadow-sm bg-white">

        <!-- Structural Cost Display Metric -->
        <div class="price-display mb-2" style="font-size:1.8rem; font-weight:800; color:var(--re-primary)">
          <c:out value="${property.formattedPrice}"/> <span style="font-size:0.9rem; color:var(--re-gray-500); font-weight:normal">/ year</span>
        </div>

        <!-- Rating Metrics Summary -->
        <c:if test="${property.totalRatings > 0}">
          <div class="d-flex align-items-center gap-2 mb-3">
            <div class="star-rating">
              <c:forEach begin="1" end="5" var="s">
                <i class="bi ${s <= property.averageRating ? 'bi-star-fill' : 'bi-star'}" style="color:#f5a623"></i>
              </c:forEach>
            </div>
            <span style="font-size:0.85rem; color:var(--re-gray-500)">
              <c:out value="${property.averageRating}"/> (<c:out value="${property.totalRatings}"/> evaluation entries)
            </span>
          </div>
        </c:if>

        <!-- Allocated Entity Owner Information Block -->
        <div class="landlord-info d-flex align-items-center gap-3 my-3 p-2 border-top border-bottom">
          <div class="landlord-avatar-placeholder" style="width:45px; height:45px; background:var(--re-primary-light); color:var(--re-primary); font-weight:bold; border-radius:50%; display:flex; align-items:center; justify-content:center">
            <c:out value="${not empty property.ownerName ? fn:toUpperCase(fn:substring(property.ownerName, 0, 1)) : 'L'}"/>
          </div>
          <div>
            <div class="landlord-name fw-bold" style="font-size:0.95rem"><c:out value="${property.ownerName}"/></div>
            <div class="landlord-role text-muted" style="font-size:0.8rem">
              ${property.verified ? '✓ Certified Landlord Profile' : 'Verified Structural Stakeholder'}
            </div>
          </div>
        </div>

        <!-- Interaction Functional Routing Area -->
        <% if (loggedIn) { %>
        <form action="<%= ctx %>/user/inquiry" method="POST" class="mb-3">
          <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
          <input type="hidden" name="propertyId" value="<c:out value='${property.propertyId}'/>">
          <textarea class="re-form-control mb-2" name="message" rows="3" placeholder="Hello, I am interested in this structural option near campus..." style="font-size:0.85rem" required></textarea>
          <button type="submit" class="btn btn-primary-brand w-100"><i class="bi bi-envelope me-1"></i> Send Immediate Inquiry</button>
        </form>

        <button class="btn btn-outline-brand w-100 mb-2" data-bs-toggle="modal" data-bs-target="#appointmentModal">
          <i class="bi bi-calendar-plus me-1"></i> Book Viewing Slot
        </button>

        <button class="btn w-100 mb-2 ${isSaved ? 'btn-secondary-brand' : 'btn-outline-brand'}" id="saveBtn2"
                onclick="toggleWishlistDetail(document.getElementById('saveBtn'), <c:out value='${property.propertyId}'/>)">
          <i class="bi ${isSaved ? 'bi-heart-fill' : 'bi-heart'} me-1"></i> ${isSaved ? 'Saved to Personal Library' : 'Save Property Profile'}
        </button>
        <% } else { %>
        <a href="<%= ctx %>/login" class="btn btn-primary-brand w-100 mb-2">
          <i class="bi bi-box-arrow-in-right me-1"></i> Login to initiate communication
        </a>
        <% } %>

        <!-- Phone Contact Pipeline Core -->
        <% if (loggedIn && request.getAttribute("property") != null) { %>
        <div class="d-flex align-items-center gap-2 mt-2 p-2 rounded bg-light border">
          <i class="bi bi-telephone-fill" style="color:var(--re-success)"></i>
          <span style="font-size:0.875rem; font-weight:600">
            <c:choose>
              <c:when test="${not empty property.ownerPhone}"><c:out value="${property.ownerPhone}"/></c:when>
              <c:otherwise>Contact channel via active submission</c:otherwise>
            </c:choose>
          </span>
        </div>
        <% } %>

        <!-- Dynamic Metrics Footprints Tracking Grid -->
        <div class="divider border-top my-3"></div>
        <div class="row text-center g-0 mt-2">
          <div class="col-4">
            <div style="font-size:1.1rem; font-weight:700; color:var(--re-primary)"><c:out value="${property.viewCount}"/></div>
            <div style="font-size:0.72rem; color:var(--re-gray-500)">Views</div>
          </div>
          <div class="col-4">
            <div style="font-size:1.1rem; font-weight:700; color:var(--re-secondary)"><c:out value="${property.saveCount}"/></div>
            <div style="font-size:0.72rem; color:var(--re-gray-500)">Saves</div>
          </div>
          <div class="col-4">
            <div style="font-size:1.1rem; font-weight:700; color:var(--re-success)"><c:out value="${property.inquiryCount}"/></div>
            <div style="font-size:0.72rem; color:var(--re-gray-500)">Inquiries</div>
          </div>
        </div>

        <div class="text-center mt-3 border-top pt-2" style="font-size:0.75rem; color:var(--re-gray-500)">
          System Registration Stamp: <fmt:formatDate value="${property.createdAt}" pattern="dd MMM yyyy"/>
        </div>

      </div>
    </div>

  </div>

  <!-- ================================================================
       SIMILAR ARCHITECTURAL TARGETS INTERACTIVE MATRIX CARD LAYER
       ================================================================ -->
  <c:if test="${not empty similarProps}">
    <div class="mt-5 pt-3 border-top">
      <h4 class="mb-4 fw-bold">Similar Alternative Structural Options</h4>
      <div class="row g-4">
        <c:forEach var="sim" items="${similarProps}">
          <div class="col-lg-3 col-md-6">
            <div class="property-card border rounded shadow-sm bg-white overflow-hidden">
              <div class="card-img-wrapper position-relative">
                <a href="<%= ctx %>/property?id=<c:out value='${sim.propertyId}'/>">
                  <img src="${not empty sim.coverImage ? ctx.concat('/').concat(sim.coverImage) : ctx.concat('/assets/images/placeholder.jpg')}"
                       alt="<c:out value='${sim.title}'/>" style="width:100%; height:180px; object-fit:cover;" loading="lazy">
                </a>
                <c:if test="${sim.verified}">
                  <div class="position-absolute top-0 start-0 m-2">
                    <span class="badge bg-success"><i class="bi bi-patch-check-fill"></i> Verified</span>
                  </div>
                </c:if>
              </div>
              <div class="card-body p-3">
                <div class="card-location text-muted" style="font-size:0.8rem">
                  <i class="bi bi-geo-alt-fill text-secondary"></i>
                  <c:out value="${sim.location != null ? sim.location.areaName : 'Eruwa Sub-Area'}"/>
                </div>
                <a href="<%= ctx %>/property?id=<c:out value='${sim.propertyId}'/>" class="card-title-link fw-bold text-decoration-none d-block my-1 text-dark" style="font-size:0.9rem">
                  <c:out value="${sim.title}"/>
                </a>
                <div class="card-footer-custom mt-2 border-top pt-2 d-flex justify-content-between align-items-center">
                  <div class="re-price fw-bold text-primary" style="font-size:0.95rem">
                    <c:out value="${sim.formattedPrice}"/> <span style="font-size:0.75rem; font-weight:normal" class="text-muted">/yr</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </c:if>

</div>

<!-- Booking Appointment Slot Modal Component Layer Wrapper -->
<div class="modal fade" id="appointmentModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content rounded-4 shadow border-0">
      <div class="modal-header border-0 pb-0">
        <h5 class="modal-title fw-bold">Schedule Structural Inspection Slot</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form action="<%= ctx %>/user/appointment" method="POST">
<input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
          <input type="hidden" name="propertyId" value="<c:out value='${property.propertyId}'/>">
          
          <div class="re-form-group mb-3">
            <label class="form-label" style="font-size:0.85rem; font-weight:600">Preferred Target Date <span class="required" style="color:var(--re-danger)">*</span></label>
            <input type="date" class="re-form-control" name="preferredDate" min="<c:out value='${requestScope.todayDate}'/>" required>
          </div>
          <div class="re-form-group mb-3">
            <label class="form-label" style="font-size:0.85rem; font-weight:600">Preferred Target Time Block (Optional)</label>
            <input type="time" class="re-form-control" name="preferredTime">
          </div>
          <div class="re-form-group mb-3">
            <label class="form-label" style="font-size:0.85rem; font-weight:600">Operational Additional Details / Notes</label>
            <textarea class="re-form-control" name="notes" rows="3" placeholder="Identify execution conditions or schedule limits targets..."></textarea>
          </div>
          <button type="submit" class="btn btn-primary-brand w-100">
            <i class="bi bi-calendar-check me-1"></i> Submit Booking Reservation Request
          </button>
        </form>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/includes/footer.jsp" />
<!-- Swiper Bundle Core JS Module -->
<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
<script src="<%= ctx %>/assets/js/compare.js"></script>

<script>
// Safely instantiate Swiper slider functionality
var swiper = new Swiper('.propertySwiper', {
  loop: true,
  pagination: { el: '.swiper-pagination', clickable: true },
  navigation: { nextEl: '.swiper-button-next', prevEl: '.swiper-button-prev' }
});

// Structural thumbnail alignment tracker configuration
swiper.on('slideChange', function() {
  document.querySelectorAll('.property-thumb').forEach(function(t, i) {
    t.style.borderColor = i === swiper.realIndex ? 'var(--re-primary)' : 'transparent';
  });
});

// Embedded Star Rating Component Layout Controller Actions
var currentRating = parseInt("<c:out value='${userRating}' default='0'/>", 10);
function setRating(val) {
  currentRating = val;
  document.getElementById('ratingInput').value = val;
  highlightStars(val);
}
function highlightStars(val) {
  document.querySelectorAll('#starRatingWidget i').forEach(function(s, i) {
    s.className = 'bi ' + (i < val ? 'bi-star-fill' : 'bi-star') + ' fs-4';
    s.style.color = i < val ? '#f5a623' : 'var(--re-gray-300)';
  });
}
function resetStars() { highlightStars(currentRating); }
if (currentRating > 0) highlightStars(currentRating);

// AJAX Wishlist state modification router link pipelines
function toggleWishlistDetail(btn, propId) {
  var ctx = document.body.dataset.ctx || '';
  fetch(ctx + '/user/wishlist', {
    method: 'POST',
    headers: {'Content-Type':'application/x-www-form-urlencoded'},
    body: 'propertyId=' + encodeURIComponent(propId)
  }).then(r => r.json()).then(data => {
    if (data.status === 'saved') {
      showToast('Saved to your dashboard wishlist tracking catalog!', 'success');
    } else if (data.status === 'removed') {
      showToast('Removed from tracking catalog records.', 'info');
    } else if (data.status === 'login_required') {
    	window.location = ctx + '/login';
    }
  }).catch(err => console.error("Communication failure context log tracking path: ", err));
}
</script>
</body>
</html>