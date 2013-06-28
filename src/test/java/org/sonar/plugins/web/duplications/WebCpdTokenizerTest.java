/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot and SonarSource
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.web.duplications;

import net.sourceforge.pmd.cpd.AbstractLanguage;
import net.sourceforge.pmd.cpd.TokenEntry;
import org.junit.Test;
import org.sonar.duplications.cpd.CPD;
import org.sonar.duplications.cpd.Match;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class WebCpdTokenizerTest {

  @Test
  public void testDuplicationOnSameFile() throws IOException {
    TokenEntry.clearImages();
    AbstractLanguage cpdLanguage = new AbstractLanguage(new WebCpdTokenizer()) {
    };
    CPD cpd = new CPD(30, cpdLanguage);
    cpd.setEncoding(Charset.defaultCharset().name());
    cpd.setLoadSourceCodeSlices(false);
    cpd.add(new File("src/test/resources/duplications/fileWithDuplications.jsp"));
    cpd.go();

    List<Match> matches = getMatches(cpd);
    assertThat(matches.size(), is(1));

    org.sonar.duplications.cpd.Match match = matches.get(0);
    assertThat(match.getLineCount(), is(16));
    assertThat(match.getFirstMark().getBeginLine(), is(227));
    assertThat(match.getSourceCodeSlice(), is(nullValue()));
  }

  private List<Match> getMatches(CPD cpd) {
    List<Match> matches = new ArrayList<org.sonar.duplications.cpd.Match>();
    Iterator<Match> matchesIter = cpd.getMatches();
    while (matchesIter.hasNext()) {
      matches.add(matchesIter.next());
    }
    return matches;
  }

}
