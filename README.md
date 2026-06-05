# QIO Integrations

Connects Mekanism QIO dashboards to AE2, Refined Storage, and Tom's Simple Storage.

See [VERSIONS.md](VERSIONS.md) for the multi-version / multi-loader layout.

## Requirements (1.20.1 Forge — implemented)

- Minecraft 1.20.1
- Forge 47.4+
- Mekanism 10.4+

Optional: AE2, Refined Storage, Tom's Simple Storage

## Development

Build the implemented target:

```bash
./gradlew :1_20_1:forge:build
./gradlew :1_20_1:forge:runClient
```

Build all enabled platforms (`1_20_1:forge`, `1_21_1:neoforge`):

```bash
./gradlew buildAll
```

Jars are copied to `deploy/` after each platform `build`.

## Tom's Simple Storage

Place an **Inventory Connector** or **Inventory Cable Connector** against the **back** of the QIO Dashboard (the side opposite the screen). The frequency must be **Public**, or Tom's cannot authenticate as a player. After connecting, wait a second for Tom's network scan (it updates every 20 ticks).

## License

All Rights Reserved, except the AE2 bridge (`QioStorageAdapter`, `QioAe2Support`), which is adapted from [Applied Mekanistics](https://github.com/AppliedEnergistics/Applied-Mekanistics) and remains **LGPL-3.0**. See [LICENSE](LICENSE).
