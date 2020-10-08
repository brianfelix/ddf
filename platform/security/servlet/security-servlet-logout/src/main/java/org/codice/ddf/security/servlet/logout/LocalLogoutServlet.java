/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.security.servlet.logout;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalLogoutServlet extends HttpServlet {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalLogoutServlet.class);

  private String redirectUrl;

  public LocalLogoutServlet() {}

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache, no-store");
    response.setHeader("Pragma", "no-cache");
    response.setContentType("application/json");
    String redirectUrlMessage = "";
    //    invalidateSession(request, response);
    try {
      redirectUrlMessage = getRedirectUrlMessage();
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (URISyntaxException e) {
      LOGGER.debug(
          "Invalid URL of {} set for logout redirect URL. Users will not be redirected to {} upon logout.",
          redirectUrl,
          redirectUrl,
          e);
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    String message =
        String.format(
            "{ \"mustCloseBrowser\": %b, \"redirectUrl\": \"%s\" }", true, redirectUrlMessage);

    try {
      response.getWriter().write(message);
      response.flushBuffer();
    } catch (IOException e) {
      LOGGER.warn("Unable to redirect to logout page.", e);
      response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String getRedirectUrlMessage() throws URISyntaxException {
    String redirectUrlMessage = "";
    if (Strings.isNotBlank(redirectUrl)) {
      URIBuilder redirectUrlBuilder = new URIBuilder(redirectUrl);
      redirectUrlMessage = redirectUrlBuilder == null ? "" : redirectUrlBuilder.build().toString();
    }
    return redirectUrlMessage;
  }

  //  private void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
  //    HttpSession session = request.getSession();
  //    if (session != null) {
  //      PrincipalHolder principalHolder =
  //              (PrincipalHolder) session.getAttribute(SecurityConstants.SECURITY_TOKEN_KEY);
  //      if (principalHolder != null) {
  //        Subject subject = ThreadContext.getSubject();
  //
  //        if (subject != null) {
  //          boolean hasSecurityAuditRole =
  //                  Arrays.stream(System.getProperty("security.audit.roles", "").split(","))
  //                          .anyMatch(subject::hasRole);
  //          if (hasSecurityAuditRole) {
  //            securityLogger.audit("Subject with admin privileges has logged out", subject);
  //          }
  //        }
  //        principalHolder.remove();
  //      }
  //      removeTokens(session.getId());
  //      session.invalidate();
  //      deleteJSessionId(response);
  //    }
  //  }

  private void deleteJSessionId(HttpServletResponse response) {
    Cookie cookie = new Cookie("JSESSIONID", "");
    cookie.setSecure(true);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setComment("EXPIRING COOKIE at " + System.currentTimeMillis());
    response.addCookie(cookie);
  }

  /** Removes OAuth tokens stored for the given session */
  //  private void removeTokens(String sessionId) {
  //    tokenStorage.delete(sessionId);
  //  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }
}
