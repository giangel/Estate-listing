<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    request.setAttribute("pageTitle", "Find Your Perfect Home Near AOPE");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en" data-ctx="<%= ctx %>">
<head>
  <jsp:include page="/includes/head-meta.jsp" />
</head>
<body data-ctx="<%= ctx %>">

<%@ include file="/includes/navbar.jsp" %>

<!-- ================================================================
     HERO SECTION
     ================================================================ -->
<section class="re-hero">
  <div class="container">
    <div class="row align-items-center">
      <div class="col-lg-8 hero-content">

        <div class="hero-eyebrow">
          <i class="bi bi-mortarboard-fill"></i>
          AOPE Student & Staff Accommodation Portal
        </div>

        <h1>
          Find Your Perfect Home<br>
          Near <span class="highlight">AOPE</span>
        </h1>

        <p class="hero-subtitle">
          Discover verified, affordable accommodation near Adeseun Ogundoyin
          Polytechnic, Eruwa. Browse self-contains, rooms, and flats with real
          images, quality indicators, and campus distance ratings.
        </p>

        <!-- Search Box Box -->
        <div class="re-search-box">
          <div class="search-tabs">
            <button class="search-tab active" type="button">All Properties</button>
            <button class="search-tab" type="button" onclick="window.location='<%= ctx %>/properties?type=1'">Self-Contain</button>
            <button class="search-tab" type="button" onclick="window.location='<%= ctx %>/properties?type=2'">Single Room</button>
            <button class="search-tab" type="button" onclick="window.location='<%= ctx %>/properties?type=3'">Mini Flat</button>
          </div>

          <form action="<%= ctx %>/properties" method="GET">
            <div class="re-search-input-group">
              
              <!-- Keyword Field -->
              <div class="search-field">
                <i class="bi bi-search"></i>
                <input type="text" class="form-control" name="keyword" id="heroSearchInput"
                       placeholder="Search by title, area, or description..."
                       value="<c:out value='${param.keyword}'/>">
              </div>
              
              <!-- Property Type Field -->
              <div class="search-field">
                <i class="bi bi-house-door"></i>
                <select class="form-select" name="type">
                  <option value="">Property Type</option>
                  <option value="1" <c:if test="${param.type == '1'}">selected</c:if>>Self-Contain</option>
                  <option value="2" <c:if test="${param.type == '2'}">selected</c:if>>Single Room</option>
                  <option value="3" <c:if test="${param.type == '3'}">selected</c:if>>Mini Flat</option>
                  <option value="4" <c:if test="${param.type == '4'}">selected</c:if>>Two-Bedroom Flat</option>
                  <option value="5" <c:if test="${param.type == '5'}">selected</c:if>>Duplex</option>
                  <option value="6" <c:if test="${param.type == '6'}">selected</c:if>>Hostel</option>
                  <option value="7" <c:if test="${param.type == '7'}">selected</c:if>>Bungalow</option>
                </select>
              </div>
              
              <!-- Budget Range Field (Mapped directly to bounds processing logic) -->
              <div class="search-field">
                <i class="bi bi-currency-exchange"></i>
                <select class="form-select" name="maxPrice">
                  <option value="">Max Budget</option>
                  <option value="50000" <c:if test="${param.maxPrice == '50000'}">selected</c:if>>Under ₦50,000</option>
                  <option value="100000" <c:if test="${param.maxPrice == '100000'}">selected</c:if>>₦50,000 – ₦100,000</option>
                  <option value="150000" <c:if test="${param.maxPrice == '150000'}">selected</c:if>>₦100,000 – ₦150,000</option>
                  <option value="200000" <c:if test="${param.maxPrice == '200000'}">selected</c:if>>₦150,000 – ₦200,000</option>
                </select>
              </div>
              
              <button type="submit" class="btn-search">
                <i class="bi bi-search"></i> Search
              </button>
            </div>
          </form>
        </div>

        <!-- Campus Distance Quick Filters Chips -->
        <div class="distance-chips mt-3">
          <a href="<%= ctx %>/properties?distance=ON_CAMPUS" class="distance-chip"><i class="bi bi-building-fill"></i> On Campus</a>
          <a href="<%= ctx %>/properties?distance=LESS_5MIN" class="distance-chip"><i class="bi bi-person-walking"></i> &lt; 5 Minutes</a>
          <a href="<%= ctx %>/properties?distance=5_TO_10MIN" class="distance-chip"><i class="bi bi-person-walking"></i> 5–10 Minutes</a>
          <a href="<%= ctx %>/properties?distance=10_TO_15MIN" class="distance-chip"><i class="bi bi-bicycle"></i> 10–15 Minutes</a>
          <a href="<%= ctx %>/properties?distance=ABOVE_15MIN" class="distance-chip"><i class="bi bi-car-front"></i> &gt; 15 Minutes</a>
        </div>

        <!-- Hero Stats Elements -->
        <div class="hero-stats">
          <div class="hero-stat"><span class="stat-num" id="heroStatListings">120+</span><span class="stat-label">Listings</span></div>
          <div class="hero-stat"><span class="stat-num" id="heroStatLandlords">45+</span><span class="stat-label">Landlords</span></div>
          <div class="hero-stat"><span class="stat-num" id="heroStatUsers">300+</span><span class="stat-label">Students Housed</span></div>
          <div class="hero-stat"><span class="stat-num">100%</span><span class="stat-label">Verified</span></div>
        </div>

      </div>
    </div>
  </div>
</section>

<!-- ================================================================
     FEATURED PROPERTIES
     ================================================================ -->
<section class="re-section section-bg-white">
  <div class="container">
    <div class="re-section-header d-flex align-items-end justify-content-between">
      <div>
        <span class="section-eyebrow">Top Picks</span>
        <h2>Featured Properties</h2>
        <p>Hand-picked verified listings with excellent facilities.</p>
      </div>
      <a href="<%= ctx %>/properties?featured=true" class="btn btn-outline-brand btn-sm-custom d-none d-md-inline-flex">
        View All <i class="bi bi-arrow-right ms-1"></i>
      </a>
    </div>

    <div class="row g-4">
      <c:choose>
        <c:when test="${not empty featuredProperties}">
          <c:forEach var="prop" items="${featuredProperties}">
            <div class="col-lg-4 col-md-6">
              <jsp:include page="/includes/property-card.jsp" />
            </div>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <%-- Production Fallbacks cards for empty server configurations --%>
          <div class="col-lg-4 col-md-6">
            <div class="property-card">
              <div class="card-img-wrapper">
                <img src="<%= ctx %>/assets/images/placeholder.jpg" alt="Property" loading="lazy">
                <div class="card-badges">
                  <span class="img-badge img-badge-featured"><i class="bi bi-star-fill"></i> Featured</span>
                  <span class="img-badge img-badge-verified"><i class="bi bi-patch-check-fill"></i> Verified</span>
                </div>
              </div>
              <div class="card-body">
                <div class="card-location"><i class="bi bi-geo-alt-fill"></i> Eruwa Town Centre</div>
                <a href="#" class="card-title-link">Modern Self-Contain Near AOPE Gate</a>
                <div class="card-specs">
                  <div class="spec-item"><i class="bi bi-door-open"></i> 1 Bed</div>
                  <div class="spec-item"><i class="bi bi-droplet"></i> Borehole</div>
                  <div class="spec-item"><i class="bi bi-person-walking"></i> &lt;5 min</div>
                </div>
                <div class="card-footer-custom">
                  <div class="re-price">₦85,000<span>/yr</span></div>
                  <div class="star-rating">
                    <div class="stars">
                      <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-half"></i>
                    </div>
                    <span class="rating-count">(12)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="col-lg-4 col-md-6">
            <div class="property-card">
              <div class="card-img-wrapper">
                <img src="<%= ctx %>/assets/images/placeholder.jpg" alt="Property" loading="lazy">
                <div class="card-badges">
                  <span class="img-badge img-badge-verified"><i class="bi bi-patch-check-fill"></i> Verified</span>
                </div>
              </div>
              <div class="card-body">
                <div class="card-location"><i class="bi bi-geo-alt-fill"></i> Eruwa Central</div>
                <a href="#" class="card-title-link">Affordable Single Room at Eruwa Town Centre</a>
                <div class="card-specs">
                  <div class="spec-item"><i class="bi bi-door-open"></i> 1 Room</div>
                  <div class="spec-item"><i class="bi bi-droplet"></i> Tap Water</div>
                  <div class="spec-item"><i class="bi bi-person-walking"></i> &lt;5 min</div>
                </div>
                <div class="card-footer-custom">
                  <div class="re-price">₦45,000<span>/yr</span></div>
                  <div class="star-rating">
                    <div class="stars">
                      <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star"></i>
                    </div>
                    <span class="rating-count">(8)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="col-lg-4 col-md-6">
            <div class="property-card">
              <div class="card-img-wrapper">
                <img src="<%= ctx %>/assets/images/placeholder.jpg" alt="Property" loading="lazy">
                <div class="card-badges">
                  <span class="img-badge img-badge-featured"><i class="bi bi-star-fill"></i> Featured</span>
                </div>
              </div>
              <div class="card-body">
                <div class="card-location"><i class="bi bi-geo-alt-fill"></i> New Layout, Eruwa</div>
                <a href="#" class="card-title-link">Two-Bedroom Flat for Staff or Family</a>
                <div class="card-specs">
                  <div class="spec-item"><i class="bi bi-door-open"></i> 2 Beds</div>
                  <div class="spec-item"><i class="bi bi-lightning-charge"></i> 24hr Power</div>
                  <div class="spec-item"><i class="bi bi-bicycle"></i> 10-15 min</div>
                </div>
                <div class="card-footer-custom">
                  <div class="re-price">₦180,000<span>/yr</span></div>
                  <div class="star-rating">
                    <div class="stars">
                      <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i>
                    </div>
                    <span class="rating-count">(5)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="text-center mt-4 d-md-none">
      <a href="<%= ctx %>/properties?featured=true" class="btn btn-outline-brand">
        View All Featured <i class="bi bi-arrow-right ms-1"></i>
      </a>
    </div>
  </div>
</section>

<!-- ================================================================
     PROPERTY CATEGORIES
     ================================================================ -->
<section class="re-section section-bg-light">
  <div class="container">
    <div class="re-section-header text-center">
      <span class="section-eyebrow">Browse by Type</span>
      <h2>Property Categories</h2>
      <p>Find the accommodation type that suits your budget and lifestyle.</p>
    </div>

    <div class="category-grid">
      <a href="<%= ctx %>/properties?type=1" class="category-card">
        <div class="cat-icon"><i class="bi bi-house-door"></i></div>
        <div class="cat-name">Self-Contain</div>
        <div class="cat-count">Most Popular</div>
      </a>
      <a href="<%= ctx %>/properties?type=2" class="category-card">
        <div class="cat-icon"><i class="bi bi-door-open"></i></div>
        <div class="cat-name">Single Room</div>
        <div class="cat-count">Budget-Friendly</div>
      </a>
      <a href="<%= ctx %>/properties?type=3" class="category-card">
        <div class="cat-icon"><i class="bi bi-building"></i></div>
        <div class="cat-name">Mini Flat</div>
        <div class="cat-count">Great Value</div>
      </a>
      <a href="<%= ctx %>/properties?type=4" class="category-card">
        <div class="cat-icon"><i class="bi bi-buildings"></i></div>
        <div class="cat-name">2-Bedroom Flat</div>
        <div class="cat-count">For Families</div>
      </a>
      <a href="<%= ctx %>/properties?type=5" class="category-card">
        <div class="cat-icon"><i class="bi bi-house-up"></i></div>
        <div class="cat-name">Duplex</div>
        <div class="cat-count">Premium</div>
      </a>
      <a href="<%= ctx %>/properties?type=6" class="category-card">
        <div class="cat-icon"><i class="bi bi-building-fill"></i></div>
        <div class="cat-name">Hostel</div>
        <div class="cat-count">Student Hostels</div>
      </a>
      <a href="<%= ctx %>/properties?type=7" class="category-card">
        <div class="cat-icon"><i class="bi bi-house-fill"></i></div>
        <div class="cat-name">Bungalow</div>
        <div class="cat-count">Spacious</div>
      </a>
    </div>
  </div>
</section>

<!-- ================================================================
     CAMPUS DISTANCE SECTION
     ================================================================ -->
<section class="re-section section-bg-white">
  <div class="container">
    <div class="re-section-header text-center">
      <span class="section-eyebrow">Campus Intelligence</span>
      <h2>Browse by Distance from AOPE</h2>
      <p>Every listing shows its walking or driving time from the AOPE main gate.</p>
    </div>

    <div class="row g-3 justify-content-center">
      <div class="col-lg-2 col-md-4 col-6">
        <a href="<%= ctx %>/properties?distance=ON_CAMPUS" class="d-block text-center p-4 rounded-3 text-decoration-none"
           style="background:rgba(25,135,84,0.08);border:2px solid rgba(25,135,84,0.2)">
          <i class="bi bi-building-fill" style="font-size:2.5rem;color:#198754"></i>
          <div class="fw-bold mt-2" style="color:#198754">On Campus</div>
          <div class="text-muted" style="font-size:0.78rem">Within Campus</div>
        </a>
      </div>
      <div class="col-lg-2 col-md-4 col-6">
        <a href="<%= ctx %>/properties?distance=LESS_5MIN" class="d-block text-center p-4 rounded-3 text-decoration-none"
           style="background:rgba(13,202,240,0.08);border:2px solid rgba(13,202,240,0.2)">
          <i class="bi bi-person-walking" style="font-size:2.5rem;color:#0dcaf0"></i>
          <div class="fw-bold mt-2" style="color:#055160">&lt; 5 Minutes</div>
          <div class="text-muted" style="font-size:0.78rem">Walking Distance</div>
        </a>
      </div>
      <div class="col-lg-2 col-md-4 col-6">
        <a href="<%= ctx %>/properties?distance=5_TO_10MIN" class="d-block text-center p-4 rounded-3 text-decoration-none"
           style="background:rgba(255,193,7,0.08);border:2px solid rgba(255,193,7,0.2)">
          <i class="bi bi-person-walking" style="font-size:2.5rem;color:#ffc107"></i>
          <div class="fw-bold mt-2" style="color:#664d03">5 – 10 Minutes</div>
          <div class="text-muted" style="font-size:0.78rem">Short Walk</div>
        </a>
      </div>
      <div class="col-lg-2 col-md-4 col-6">
        <a href="<%= ctx %>/properties?distance=10_TO_15MIN" class="d-block text-center p-4 rounded-3 text-decoration-none"
           style="background:rgba(253,126,20,0.08);border:2px solid rgba(253,126,20,0.2)">
          <i class="bi bi-bicycle" style="font-size:2.5rem;color:#fd7e14"></i>
          <div class="fw-bold mt-2" style="color:#6a2c00">10 – 15 Minutes</div>
          <div class="text-muted" style="font-size:0.78rem">Bicycle / Okada</div>
        </a>
      </div>
      <div class="col-lg-2 col-md-4 col-6">
        <a href="<%= ctx %>/properties?distance=ABOVE_15MIN" class="d-block text-center p-4 rounded-3 text-decoration-none"
           style="background:rgba(220,53,69,0.08);border:2px solid rgba(220,53,69,0.2)">
          <i class="bi bi-car-front" style="font-size:2.5rem;color:#dc3545"></i>
          <div class="fw-bold mt-2" style="color:#7a1020">&gt; 15 Minutes</div>
          <div class="text-muted" style="font-size:0.78rem">Transport Required</div>
        </a>
      </div>
    </div>
  </div>
</section>

<!-- ================================================================
     POPULAR AREAS
     ================================================================ -->
<section class="re-section section-bg-light">
  <div class="container">
    <div class="re-section-header">
      <span class="section-eyebrow">Neighbourhoods</span>
      <h2>Popular Areas Near AOPE</h2>
      <p>Discover the most sought-after neighbourhoods for students and staff.</p>
    </div>

    <div class="row g-4">
      <div class="col-lg-3 col-md-6">
        <a href="<%= ctx %>/properties?keyword=Eruwa+Town+Centre" class="area-card">
          <div class="area-img">
            <i class="bi bi-buildings"></i>
            <div class="area-badge-overlay">
              <span class="re-badge re-badge-distance"><i class="bi bi-person-walking"></i> &lt;5 min</span>
            </div>
          </div>
          <div class="area-body">
            <div class="area-name">Eruwa Town Centre</div>
            <p style="font-size:0.8rem;color:var(--re-gray-500);margin:0">Close to AOPE main gate. Markets, banks, and shops nearby.</p>
            <div class="area-meta">
              <span><i class="bi bi-house me-1"></i>32 listings</span>
              <span class="re-badge re-badge-verified"><i class="bi bi-shield-check"></i> Safe</span>
            </div>
          </div>
        </a>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <a href="<%= ctx %>/properties?keyword=New+Layout" class="area-card">
          <div class="area-img" style="background:linear-gradient(135deg,#0f6036,#198754)">
            <i class="bi bi-house-fill"></i>
            <div class="area-badge-overlay">
              <span class="re-badge re-badge-distance"><i class="bi bi-bicycle"></i> 10-15 min</span>
            </div>
          </div>
          <div class="area-body">
            <div class="area-name">New Layout</div>
            <p style="font-size:0.8rem;color:var(--re-gray-500);margin:0">Quiet residential area with good road access and modern houses.</p>
            <div class="area-meta">
              <span><i class="bi bi-house me-1"></i>18 listings</span>
              <span class="re-badge re-badge-verified"><i class="bi bi-shield-check"></i> Safe</span>
            </div>
          </div>
        </a>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <a href="<%= ctx %>/properties?keyword=Aduloju" class="area-card">
          <div class="area-img" style="background:linear-gradient(135deg,#0a5c8a,#2a5f96)">
            <i class="bi bi-building"></i>
            <div class="area-badge-overlay">
              <span class="re-badge re-badge-distance"><i class="bi bi-person-walking"></i> 5-10 min</span>
            </div>
          </div>
          <div class="area-body">
            <div class="area-name">Aduloju Area</div>
            <p style="font-size:0.8rem;color:var(--re-gray-500);margin:0">Popular among students. Affordable rooms and self-contains available.</p>
            <div class="area-meta">
              <span><i class="bi bi-house me-1"></i>24 listings</span>
              <span class="re-badge re-badge-pending"><i class="bi bi-shield"></i> Moderate</span>
            </div>
          </div>
        </a>
      </div>
      
      <div class="col-lg-3 col-md-6">
        <a href="<%= ctx %>/properties?keyword=Staff+Quarters" class="area-card">
          <div class="area-img" style="background:linear-gradient(135deg,#6a0dad,#9b30d9)">
            <i class="bi bi-shield-check"></i>
            <div class="area-badge-overlay">
              <span class="re-badge re-badge-distance"><i class="bi bi-building-fill"></i> On Campus</span>
            </div>
          </div>
          <div class="area-body">
            <div class="area-name">AOPE Staff Quarters</div>
            <p style="font-size:0.8rem;color:var(--re-gray-500);margin:0">Exclusive staff housing within the polytechnic premises.</p>
            <div class="area-meta">
              <span><i class="bi bi-house me-1"></i>8 listings</span>
              <span class="re-badge re-badge-verified"><i class="bi bi-shield-check"></i> Very Safe</span>
            </div>
          </div>
        </a>
      </div>
    </div>
  </div>
</section>

<!-- ================================================================
     STUDENT BUDGET SECTION
     ================================================================ -->
<section class="re-section section-bg-white">
  <div class="container">
    <div class="re-section-header text-center">
      <span class="section-eyebrow">Budget Friendly</span>
      <h2>Find a Home Within Your Budget</h2>
      <p>Filter properties by the AOPE student accommodation budget categories.</p>
    </div>
    <div class="row g-3">
      <div class="col-lg col-md-4 col-6">
        <a href="<%= ctx %>/properties?maxPrice=50000" class="d-block p-3 rounded-3 text-center text-decoration-none"
           style="background:#f8f9fa;border:2px solid #dee2e6;transition:all 0.3s">
          <div class="fw-bold" style="color:#198754;font-size:1.1rem">₦0 – ₦50k</div>
          <div style="font-size:0.8rem;color:#6c757d;margin-top:4px">Starter Budget</div>
        </a>
      </div>
      <div class="col-lg col-md-4 col-6">
        <a href="<%= ctx %>/properties?minPrice=50000&maxPrice=100000" class="d-block p-3 rounded-3 text-center text-decoration-none"
           style="background:#f8f9fa;border:2px solid #dee2e6;transition:all 0.3s">
          <div class="fw-bold" style="color:#0dcaf0;font-size:1.1rem">₦50k – ₦100k</div>
          <div style="font-size:0.8rem;color:#6c757d;margin-top:4px">Most Popular</div>
        </a>
      </div>
      <div class="col-lg col-md-4 col-6">
        <a href="<%= ctx %>/properties?minPrice=100000&maxPrice=150000" class="d-block p-3 rounded-3 text-center text-decoration-none"
           style="background:#f8f9fa;border:2px solid #dee2e6;transition:all 0.3s">
          <div class="fw-bold" style="color:#fd7e14;font-size:1.1rem">₦100k – ₦150k</div>
          <div style="font-size:0.8rem;color:#6c757d;margin-top:4px">Mid Range</div>
        </a>
      </div>
      <div class="col-lg col-md-4 col-6">
        <a href="<%= ctx %>/properties?minPrice=150000&maxPrice=200000" class="d-block p-3 rounded-3 text-center text-decoration-none"
           style="background:#f8f9fa;border:2px solid #dee2e6;transition:all 0.3s">
          <div class="fw-bold" style="color:#0d6efd;font-size:1.1rem">₦150k – ₦200k</div>
          <div style="font-size:0.8rem;color:#6c757d;margin-top:4px">Comfort Level</div>
        </a>
      </div>
      <div class="col-lg col-md-4 col-6">
        <a href="<%= ctx %>/properties?minPrice=200000" class="d-block p-3 rounded-3 text-center text-decoration-none"
           style="background:#f8f9fa;border:2px solid #dee2e6;transition:all 0.3s">
          <div class="fw-bold" style="color:#dc3545;font-size:1.1rem">₦200k+</div>
          <div style="font-size:0.8rem;color:#6c757d;margin-top:4px">Premium</div>
        </a>
      </div>
    </div>
  </div>
</section>

<!-- ================================================================
     TESTIMONIALS
     ================================================================ -->
<section class="re-section section-bg-light">
  <div class="container">
    <div class="re-section-header text-center">
      <span class="section-eyebrow">Success Stories</span>
      <h2>What Students Say</h2>
    </div>
    <div class="row g-4">
      <div class="col-lg-4 col-md-6">
        <div class="testimonial-card">
          <div class="quote-icon">&ldquo;</div>
          <p class="testimonial-text">
            I found a verified self-contain within 3 minutes of campus for just
            ₦85,000. The quality indicators told me exactly what to expect -
            borehole water, partial electricity, and good security. No surprises!
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">CO</div>
            <div>
              <div class="author-name">Chidinma Okafor</div>
              <div class="author-role">ND1 Computer Science</div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-4 col-md-6">
        <div class="testimonial-card">
          <div class="quote-icon">&ldquo;</div>
          <p class="testimonial-text">
            As a landlord, this platform brought me 5 serious tenants in one week.
            The inquiry system is clean and professional. No more phone calls from
            unserious people!
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">AO</div>
            <div>
              <div class="author-name">Adebayo Olusola</div>
              <div class="author-role">Verified Landlord, Eruwa</div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-lg-4 col-md-6">
        <div class="testimonial-card">
          <div class="quote-icon">&ldquo;</div>
          <p class="testimonial-text">
            The roommate matching feature saved me! I found a course mate from the
            same department who needed someone to share a mini flat. We moved in
            together and each pay ₦55,000 instead of ₦110,000.
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">TA</div>
            <div>
              <div class="author-name">Temitayo Adesanya</div>
              <div class="author-role">ND2 Accountancy</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- ================================================================
     CALL TO ACTION
     ================================================================ -->
<section class="re-cta">
  <div class="container text-center" style="position:relative;z-index:2">
    <span class="section-eyebrow" style="color:rgba(232,160,32,0.9)">Property Owners</span>
    <h2 class="mb-3">List Your Property Free Today</h2>
    <p class="mb-4 mx-auto">
      Reach hundreds of AOPE students and staff actively looking for accommodation.
      Free listing, verified badge, and inquiry management - all in one platform.
    </p>
    <div class="d-flex gap-3 justify-content-center flex-wrap">
<a href="<%= ctx %>/register" class="btn btn-secondary-brand btn-lg-custom">
        <i class="bi bi-plus-circle me-2"></i> List Your Property
      </a>
      <a href="<%= ctx %>/properties" class="btn btn-outline-brand btn-lg-custom" style="border-color:rgba(255,255,255,0.5);color:white">
        <i class="bi bi-building me-2"></i> Browse Properties
      </a>
    </div>
  </div>
</section>

<jsp:include page="/includes/footer.jsp" />

<!-- Dependencies Bundle scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>

</body>
</html>