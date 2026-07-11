# earlyeffect.rocks

Org hub for [early-effect](https://github.com/early-effect) libraries.
Built with [specular](https://github.com/early-effect/specular) from published micro-site
`metadata.json` URLs listed in [`catalog-urls.txt`](catalog-urls.txt).

## Brand mark

| File | Use |
|------|-----|
| [`images/logo.svg`](images/logo.svg) | **Source of truth** for adaptation (CSS variables for colors; transparent-friendly) |
| [`images/logo.png`](images/logo.png) | Raster hero/favicon companion (1024²) |

Edit the SVG `:root` variables (`--ee-trace`, `--ee-pad`, `--ee-bg`, …) to retheme.
Remove or clear the `.bg` rect for a transparent embed.

## Local build

```bash
sbt specularSite
# → target/site
```

## Deploy

1. Enable **Settings → Pages → Source: GitHub Actions**
2. **Actions → Hub site → Run workflow** (manual)

Custom domain: `www.earlyeffect.rocks` (see `CNAME`).

## Adding a library

1. Ship docs via `early-effect/.github` → `specular-docs.yml` so
   `https://early-effect.github.io/<repo>/metadata.json` exists
2. Append that URL to `catalog-urls.txt`
3. Run **Hub site**
