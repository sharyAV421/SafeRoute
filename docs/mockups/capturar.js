const { chromium } = require('playwright');
const fs = require('node:fs/promises');
const path = require('node:path');

const root = __dirname;
const html = path.join(root, 'html');
const out = path.join(root, 'img');
const pages = [
  'index', '01-acceso', '02-roles', '03-infraestructura', '04-conexiones',
  '05-destinos', '06-emergencias', '07-zonas', '08-panel-emergencias',
  '09-configuracion-riesgo', '10-caracteristicas-riesgo', '11-prioridades',
  '12-mapa-riesgos', '13-ubicacion', '14-calculo-ruta', '15-resultado-ruta',
  '16-detalle-ruta'
];

(async () => {
  await fs.mkdir(out, { recursive: true });
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 1000 }, deviceScaleFactor: 1 });
  for (const name of pages) {
    await page.goto(`file://${path.join(html, name === 'index' ? 'index.html' : `${name}.html`)}`, { waitUntil: 'networkidle' });
    await page.screenshot({ path: path.join(out, `${name}.png`), fullPage: true });
  }
  await browser.close();
  console.log(`Capturas generadas: ${pages.length}`);
})().catch(err => { console.error(err.message); process.exitCode = 1; });
