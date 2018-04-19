[![Known Vulnerabilities](https://snyk.io/test/github/frode-carlsen/fc-timeseries/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/frode-carlsen/fc-timeseries?targetFile=pom.xml)

# fc-timeseries
Basic timeseries library for managing simple time-variant datatypes of different flavours. Pluggable functions and calculators.

Useful for representing a time series curve composed of multiple, variable duration time segments, with each it's own value function.

# Use cases:

* representing timeseries similar to ENTOSO-E (https://www.entsoe.eu/Documents/EDI/Library/depreciated/10_Timeseries-curve-types_v1r1.pdf) 
* Calculating timeseries with different number representations (BigDecimal, Double, Long) using plus/minus/divide/multiply operations
* Define custom operations on timeseries

The libary implements timeseries as a segquence of timesegments, emphasizing a simple, basic solution but with flexibility in calculation on values.

For  larger timeseries (10k-100k or more points) and for timeseries with constant intervals (e.g millisecond precision), 
this will not be the most efficient.


