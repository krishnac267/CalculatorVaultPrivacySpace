# Performance Report — v3.0.0

## Benchmarks (`PerformanceBenchmarkTest`)

| Operation | Target | Method |
|-----------|--------|--------|
| Note encryption (avg) | < 50 ms | 10 iterations, ~400 char payload |
| Note decryption (avg) | < 50 ms | 10 iterations |

Run on physical device or API 34 emulator for representative Keystore latency.

## Additional Observations

| Metric | Expected | Notes |
|--------|----------|-------|
| Cold launch | < 2 s | Not automated — profile with Android Studio |
| PIN unlock → vault | < 500 ms | Dominated by Compose navigation |
| File import (10 MB) | < 3 s | Stream encryption; 100 MB cap |
| Room search | < 100 ms | Indexed title + searchText |

## Recommendations

1. Add Macrobenchmark module for cold start in v3.1
2. Lazy-load Files tab thumbnails
3. Paginate notification history queries
