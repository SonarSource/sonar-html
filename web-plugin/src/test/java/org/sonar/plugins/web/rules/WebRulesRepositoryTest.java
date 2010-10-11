/*
 * Copyright (C) 2010 Matthijs Galesloot
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.web.rules;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparisons.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.check.Rule;
import org.sonar.plugins.web.AbstractWebPluginTester;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.rules.web.DefaultWebProfile;
import org.sonar.plugins.web.rules.web.JSFProfile;
import org.sonar.plugins.web.rules.web.JSPProfile;
import org.sonar.plugins.web.rules.web.WebProfileExporter;
import org.sonar.plugins.web.rules.web.WebProfileImporter;
import org.sonar.plugins.web.rules.web.WebRulesRepository;

/**
 * @author Matthijs Galesloot
 */
public class WebRulesRepositoryTest extends AbstractWebPluginTester {

  @Test
  public void createDefaultWebProfile() {
    ProfileDefinition profileDefinition = new DefaultWebProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void createJSFProfile() {
    ProfileDefinition profileDefinition = new JSFProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void createJSPProfile() {
    ProfileDefinition profileDefinition = new JSPProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    assertThat(profile.getActiveRulesByRepository(WebRulesRepository.REPOSITORY_KEY).size(), greaterThan(1));
    assertThat(validationMessages.hasErrors(), is(false));
  }

  @Test
  public void initializeWebRulesRepository() {
    WebRulesRepository rulesRepository = new WebRulesRepository(newServerFileSystem());

    assertTrue(rulesRepository.createRules().size() > 20);
  }

  @Test
  public void createChecks() {
    ProfileDefinition profileDefinition = new DefaultWebProfile(newRuleFinder());
    ValidationMessages validationMessages = ValidationMessages.create();
    RulesProfile profile = profileDefinition.createProfile(validationMessages);

    List<AbstractPageCheck> checks = WebRulesRepository.createChecks(profile);

    // check annotation
    for (AbstractPageCheck check : checks) {
      Rule rule = check.getClass().getAnnotation(Rule.class);
      assertNotNull(rule.key());
      assertNotNull(rule.name());
      assertNotNull(rule.isoCategory());
    }
    assertTrue(checks.size() > 20);
  }

  @Test
  public void exportImportProfile() throws FileNotFoundException {
    ValidationMessages validationMessages = ValidationMessages.create();

    // import rules
    String path = "org/sonar/plugins/web/rules/web/jsf-rules.xml";
    Reader reader = new InputStreamReader(JSFProfile.class.getClassLoader().getResourceAsStream(path));
    RulesProfile rulesProfile1 = new WebProfileImporter(newRuleFinder()).importProfile(reader, validationMessages);

    // export the rules to xml
    StringWriter writer = new StringWriter();
    new WebProfileExporter(newConfiguration()).exportProfile(rulesProfile1, writer);
    assertNotNull(writer.getBuffer().toString());

    reader = new StringReader(writer.getBuffer().toString());
    RulesProfile rulesProfile2 = new WebProfileImporter(newRuleFinder()).importProfile(reader, validationMessages);

    assertNotSame(rulesProfile1.getActiveRules(), rulesProfile2.getActiveRules());
    assertEquals(rulesProfile1.getActiveRules().size(), rulesProfile2.getActiveRules().size());
  }

  private Configuration newConfiguration() {
    return new Configuration() {

      public Configuration subset(String prefix) {
        // TODO Auto-generated method stub
        return null;
      }

      public void setProperty(String key, Object value) {
        // TODO Auto-generated method stub

      }

      public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
      }

      public String[] getStringArray(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public String getString(String key, String defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public String getString(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public Short getShort(String key, Short defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public short getShort(String key, short defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public short getShort(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public Object getProperty(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public Properties getProperties(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public Long getLong(String key, Long defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public long getLong(String key, long defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public long getLong(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public List getList(String key, List defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public List getList(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public Iterator getKeys(String prefix) {
        // TODO Auto-generated method stub
        return null;
      }

      public Iterator getKeys() {
        // TODO Auto-generated method stub
        return null;
      }

      public Integer getInteger(String key, Integer defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public int getInt(String key, int defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public int getInt(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public Float getFloat(String key, Float defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public float getFloat(String key, float defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public float getFloat(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public Double getDouble(String key, Double defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public double getDouble(String key, double defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public double getDouble(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public Byte getByte(String key, Byte defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public byte getByte(String key, byte defaultValue) {
        // TODO Auto-generated method stub
        return 0;
      }

      public byte getByte(String key) {
        // TODO Auto-generated method stub
        return 0;
      }

      public Boolean getBoolean(String key, Boolean defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public boolean getBoolean(String key, boolean defaultValue) {
        // TODO Auto-generated method stub
        return false;
      }

      public boolean getBoolean(String key) {
        // TODO Auto-generated method stub
        return false;
      }

      public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public BigInteger getBigInteger(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        // TODO Auto-generated method stub
        return null;
      }

      public BigDecimal getBigDecimal(String key) {
        // TODO Auto-generated method stub
        return null;
      }

      public boolean containsKey(String key) {
        // TODO Auto-generated method stub
        return false;
      }

      public void clearProperty(String key) {
        // TODO Auto-generated method stub

      }

      public void clear() {
        // TODO Auto-generated method stub

      }

      public void addProperty(String key, Object value) {
        // TODO Auto-generated method stub

      }
    };
  }

}
