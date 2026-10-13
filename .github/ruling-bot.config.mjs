// HTML defaults loaded from the exact tested checkout by updater and reporter.
import { defaults } from './actions/ruling_bot/config.mjs';

export const rulingConfig = {
  ...defaults,
  'new-results-path': 'its/ruling/target/actual',
  'old-results-path': 'its/ruling/src/test/resources/expected',
  'sources-path': 'its/sources',
  'sources-repo-url': 'https://github.com/SonarCommunity/web-test-sources/blob/master',
  'results-artifact-name': 'actual_html',
};
