import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import { environmentConfiguration } from './config.mjs';

const config = await environmentConfiguration(process.env);
const settings = Object.fromEntries(Object.entries(config).map(([name, value]) => [
  name.replaceAll('-', '_').toUpperCase(), String(value),
]));
execFileSync(process.execPath, [
  fileURLToPath(new URL('generate-report.mjs', import.meta.url)), config['old-results-path'],
], {
  stdio: 'inherit',
  env: { ...process.env, ...settings },
});
