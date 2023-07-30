library(lubridate)
library(tseries)
library(tidyverse)
library(ggplot2)
library(dplyr)
library(readr)
library(gridExtra)
library(matrixcalc)
library(MTS)

ec <- read_csv("MER_ResEnergyConsumption.csv", col_names = TRUE, show_col_types = FALSE)

## [2] "Natural Gas Consumed by the Residential Sector (Excluding Supplemental Gaseous Fuels) ==  NNRCBUS"
## [3] "Petroleum Consumed by the Residential Sector == PARCBUS"

##drop_na is to remove the information for month "13" which is the summary of the year
energy_tbl <- ec %>% rowwise() %>% filter(MSN %in% c("NNRCBUS", "PARCBUS")) %>% select(YYYYMM, Value, MSN) %>% mutate(date=ym(YYYYMM)) %>% drop_na() %>% select(-c("YYYYMM")) %>% mutate(Value=as.numeric(Value))
ggplot(energy_tbl, aes(x = date, y = Value, col = MSN)) + geom_line() +
  ggtitle("Petroleum and Natural gas Residential consumption") +
  scale_x_date(date_labels = "%b %Y", limit=c(as.Date("1973-01-01"),as.Date("2021-10-01")) ) +
  theme(axis.text.x=element_text(angle=60, hjust=1)) +
  theme(legend.justification=c(0,0), legend.position=c(0,0))

energy_tbl <- energy_tbl %>% pivot_wider(names_from = MSN, values_from = Value)

check_stationarity <- function(ts) {
  ts <- as.ts(ts)
  time <- 1:length(ts)
  lm_fit <- lm(ts ~ time)
  if (coef(summary(lm_fit))[,"Pr(>|t|)"]["time"] < 0.01) {
    ts <- ts - lm_fit$fitted.values
  }
  spec_density <- spectrum(ts)
  max_freq <- spec_density$freq[which.max(spec_density$spec)]
  time_period <- 1/max_freq
  sprintf("Frequency f: %.5f and periodicity %.2f (months)", max_freq, time_period)

  d.ts <- diff(ts, lag = time_period)
  ## Augmented Dickey-Fuller Test
  adf.test <- adf.test(d.ts)
  print(adf.test)
  if (adf.test[["statistic"]] < 0 && adf.test[["p.value"]] <= 0.01)
    return(d.ts)
  return(NULL)
}

### Checking for stationarity
PARCBUS_ts <- check_stationarity(energy_tbl[,"PARCBUS"])
NNRCBUS_ts <- check_stationarity(energy_tbl[,"NNRCBUS"])

par(mfrow = c(2, 1))
spectrum(as.ts(PARCBUS_ts), main = "Residual spectral density - Petroleum data")
spectrum(as.ts(NNRCBUS_ts), main = "Residual spectral density - Natural gas data")

## Autocorrelation plots

grid.arrange(ggAcf(PARCBUS_ts), ggAcf(NNRCBUS_ts), ggPacf(PARCBUS_ts), ggPacf(NNRCBUS_ts), ncol=2)


## Multivariate time analysis
## zt = (PARCBUS_dt, PARCBUS_dt) is said to be weakly stationary if E(zt) = mu (constant mean) and
## Cov(zt) is a positive-definite matrix

## 1. the definition of stationarity for multivariate time series
energy_mts = data.frame(PARCBUS = PARCBUS_dt, NNRCBUS = NNRCBUS_dt)
mu = c(mean(energy_mts[, "PARCBUS"]), mean(energy_mts[,"NNRCBUS"]))
Sigma_z = cov(energy_mts)
cor(energy_mts)
cor(PARCBUS_dt, NNRCBUS_dt)
is.positive.definite(Sigma_z)

## 2. estimation of the sample cross-correlation matrix (CCM)
## This cross-covariance matrix is a function of l, not the time index t, because zt
## is stationary. For l = 0, we have the covariance matrix Γ(0) of zt. In some cases, we use the notation Σz to denote the covariance matrix of zt, that is, Σz = Γ0.

## a positive lag l, Omega(l,ij) can be regarded as a measure of the linear dependence of
## the ith component zit on the lth lagged value of the jth component zjt.

## Unlike the case of univariate stationary time series for which the auto- covariances
## of lag l and lag −l are identical, one must take the transpose of a positive-lag
## cross-covariance matrix to obtain the negative-lag cross-covariance matrix.

## CCM(l) == CCM(-l)

## To study the linear dynamic dependence between the components of zt, it
## suffices to consider CCM(l) for l ≥ 0

## Specifically, for each (i, j)th position, we plot CCM(l,ij) versus l.
## This plot shows the linear dynamic dependence of zit on zj,t−l for l = 0,m
## We refer to these k^2 plots as the cross-correlation plots of zt.

## simplified matrix s(l)


## This is a generalization of the sample autocorrelation function (ACF) of the univari-
##  ate time series. For a k-dimensional series zt, we have k2 plots. The 95% interval is
## often computed using 0 ± 2/sqrt(T). In other words, we use 1/sqrt(T) as the standard error
## for the sample cross-correlations.


MTSplot(energy_mts)
ccm(energy_mts, lag=20)


## => not a white noise series

## 3.multivariate Portmanteau test for zero-valued cross-correlations
## A basic test in multivariate time series analysis is to detect the existence of linear dynamic
## dependence in the data. This amounts to testing the null hypothesis H0 : rho_1 = ··· = rho_m = 0
## versus the alternative hypothesis Ha : rho_i != 0 for some i satisfying 1 ≤ i ≤ m, where m is a
## positive integer.

mq(energy_mts,lag=20)
## Ljung-Box Statistics:
## m       Q(m)     df    p-value
## [1,]     1       251       4        0
## [2,]     2       347       8        0
## [3,]     3       387      12        0
## [4,]     4       413      16        0
## [5,]     5       437      20        0
## [6,]     6       455      24        0
## [7,]     7       461      28        0
## [8,]     8       473      32        0
## [9,]     9       486      36        0
## [10,]    10       498      40       0

## In particular, we can compute the p-value of the chisquared statistic
## for testing H0 : Omega(l) = 0 versus Ha : Omega(l) != 0. By plotting the
## p-value against the lag, we obtain a multivariate generalization of the
## ACF plot.

## The dashed line of the plot denotes the type I error of 5%.



