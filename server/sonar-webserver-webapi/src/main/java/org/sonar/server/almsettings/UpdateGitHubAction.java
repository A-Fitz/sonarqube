/*
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.almsettings;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.alm.setting.AlmSettingDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.user.UserSession;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class UpdateGitHubAction implements AlmSettingsWsAction {

  private static final String PARAM_KEY = "key";
  private static final String PARAM_NEW_KEY = "newKey";
  private static final String PARAM_URL = "url";
  private static final String PARAM_APP_ID = "appId";
  private static final String PARAM_PRIVATE_KEY = "privateKey";

  private final DbClient dbClient;
  private final UserSession userSession;

  public UpdateGitHubAction(DbClient dbClient, UserSession userSession) {
    this.dbClient = dbClient;
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("update_github")
      .setDescription("Update GitHub ALM instance Setting. <br/>" +
        "Requires the 'Administer System' permission")
      .setPost(true)
      .setSince("8.1")
      .setHandler(this);

    action.createParam(PARAM_KEY)
      .setRequired(true)
      .setMaximumLength(40)
      .setDescription("Unique key of the GitHub instance setting");
    action.createParam(PARAM_NEW_KEY)
      .setRequired(false)
      .setMaximumLength(40)
      .setDescription("Optional new value for an unique key of the GitHub instance setting");
    action.createParam(PARAM_URL)
      .setRequired(true)
      .setMaximumLength(2000)
      .setDescription("GitHub API URL");
    action.createParam(PARAM_APP_ID)
      .setRequired(true)
      .setMaximumLength(80)
      .setDescription("GitHub API ID");
    action.createParam(PARAM_PRIVATE_KEY)
      .setRequired(true)
      .setMaximumLength(2000)
      .setDescription("GitHub App private key");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    userSession.checkIsSystemAdministrator();
    doHandle(request);
    response.noContent();
  }

  private void doHandle(Request request) {
    String key = request.mandatoryParam(PARAM_KEY);
    String newKey = request.param(PARAM_NEW_KEY);
    String url = request.mandatoryParam(PARAM_URL);
    String appId = request.mandatoryParam(PARAM_APP_ID);
    String privateKey = request.mandatoryParam(PARAM_PRIVATE_KEY);

    try (DbSession dbSession = dbClient.openSession(false)) {

      AlmSettingDto almSettingDto = dbClient.almSettingDao().selectByKey(dbSession, key)
        .orElseThrow(() -> new NotFoundException(format("No ALM setting with key '%s' has been found", key)));
      if (isNotBlank(newKey) && !newKey.equals(key)) {
        dbClient.almSettingDao().selectByKey(dbSession, newKey)
          .ifPresent(almSetting -> {
            throw new IllegalArgumentException(format("ALM setting with key '%s' already exists", almSetting.getKey()));
          });
      }
      dbClient.almSettingDao().update(dbSession, almSettingDto
        .setKey(isNotBlank(newKey) ? newKey : key)
        .setUrl(url)
        .setAppId(appId)
        .setPrivateKey(privateKey));
      dbSession.commit();
    }
  }

}