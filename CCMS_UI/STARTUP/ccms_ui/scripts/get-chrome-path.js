#!/usr/bin/env node
const puppeteer = require('puppeteer');

(async () => {
  try {
    const path = await puppeteer.executablePath();
    process.stdout.write(path);
  } catch (err) {
    process.stderr.write(`Could not resolve puppeteer Chrome path: ${err.message}\n`);
    process.exit(1);
  }
})();
