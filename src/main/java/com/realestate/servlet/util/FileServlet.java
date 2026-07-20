package com.realestate.servlet.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * FileServlet - Serves uploaded property images and profile photos
 * from an external directory outside the deployed webapp, so files
 * survive Eclipse "Clean" / republish cycles.
 *
 * GET /uploads/*  ->  streams the requested file from UPLOAD_BASE_DIR
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
@WebServlet("/uploads/*")
public class FileServlet extends HttpServlet {

    // MUST match the uploadDir used in CreatePropertyServlet and ProfileServlet
    private static final String UPLOAD_BASE_DIR =
        System.getProperty("user.home") + File.separator + "aope-estate-uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // e.g. /properties/5/abc123.jpg
        if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Prevent directory traversal attacks
        String safePath = pathInfo.replace("..", "");
        File file = new File(UPLOAD_BASE_DIR, safePath);

        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        resp.setContentType(mimeType);
        resp.setContentLengthLong(file.length());
        resp.setHeader("Cache-Control", "public, max-age=86400");

        try (var in = Files.newInputStream(file.toPath())) {
            in.transferTo(resp.getOutputStream());
        }
    }
}