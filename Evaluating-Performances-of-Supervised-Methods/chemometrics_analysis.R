# Data preparation and exploratory analysis
library(tidyverse) # for dplyr tibble operations and ggplot
mass_fractions <- read.csv("~/Desktop/mass_fractions.csv") # read the csv file
glimpse(mass_fractions) # examining the data set

mass_fractions <- mass_fractions %>%
  mutate(date=as.Date(date), catalyst=as.factor(catalyst)) %>% # change these two column types
  mutate(response=out_fraction_13+out_fraction_14+out_fraction_15) # get the response by summing up the 3 density ranges

## visualize the distribution of the response (sum of out_fraction_13 to 15)
ggplot(mass_fractions) +
  geom_histogram(aes(response), bins = 20, fill="orange", col="red") + # generate histogram
  labs(title="Empirical Distribution of Sum of Target Range Densities",
       x="Mass Fraction of Sum of Target Range",
       y="Frequency") + # set up labels
  theme_minimal()

## visulize the correlation between catalyst (categorical variable) and response
ggplot(mass_fractions, aes(y=response, x=catalyst)) + 
  geom_boxplot(fill="orange", col="red") + # generate boxplot
  labs(title="Catalyst Types versus Target Range Yield",
       x="Catalyst Type",
       y="Mass Fraction of Sum of Target Range") + # set up labels
  theme_minimal()

## visualize the correlation between temperature and response plus linear regression line
plot1 <- ggplot(mass_fractions, aes(y=response, x=temperature)) + 
  geom_line(col="orange") + # generate line graph
  geom_smooth(method='lm', formula=y~x) + 
  labs(title="Catalyst Types versus Target Range Yield",
       x="Reactor Temperature (in degrees Fahrenheit)",
       y="Mass Fraction of Sum of Target Range") + # set up labels
  theme_minimal()

## visualize the correlation between through_time and response plus linear regression line
plot2 <- ggplot(mass_fractions, aes(y=response, x=through_time)) + 
  geom_line(col="orange") + # generate line graph
  geom_smooth(method='lm', formula=y~x) + 
  labs(title="Reaction Time versus Target Range Yield",
       x="Reaction Time",
       y="Mass Fraction of Sum of Target Range") + # set up labels
  theme_minimal()
gridExtra::grid.arrange(plot1, plot2, ncol=2)

## visualize the distribution of feed fractions
feed_fractions <- mass_fractions %>%
  select(date, matches("feed_fraction_")) %>% # first extract only feed_fraction columns
  pivot_longer(-date, names_to = "feed_fraction", values_to = "density") %>% # make column names become factor levels of a new column
  mutate(feed_fraction=as.factor(feed_fraction)) # change it to factor
ggplot(feed_fractions, aes(y=density, x=reorder(feed_fraction, -density))) + 
  geom_boxplot() + # generate boxplot
  labs(title="Distribution of Feed Mixture Mass Fraction over 20 Intervals",
       x="Feed Composition Interval",
       y="Mass Fraction") + # set up labels
  theme(axis.text.x=element_text(angle=60, hjust=1))

## visualize the correlation between feed_fraction 13-15 and the response
mass_fractions <-  mass_fractions %>% # change these two column types
  mutate(feed_13_to_15=feed_fraction_13+feed_fraction_14+feed_fraction_15)
ggplot(mass_fractions, aes(y=response, x=feed_13_to_15)) + 
  geom_line(col="orange") + # generate line graph
  geom_smooth(method='lm', formula=y~x) + 
  labs(title="Target Range Feed versus Target Range Yield",
       x="Target Range Total Feed",
       y="Target Range Total Yield") + # set up labels
  theme_minimal()

## prepare the data to get rid of all other useless out_fraction columns for modelling
mass_fractions_mod <- mass_fractions %>%
  select(catalyst, temperature, through_time, matches("feed_fraction_"), response)
## train test separation according to a 70:30 ratio
set.seed(123456) # set seed for sample reproducibility
train_fractions <- sample_frac(mass_fractions_mod, 0.7)
test_fractions <- mass_fractions_mod %>% anti_join(train_fractions)

# Methods and results
## linear regression
set.seed(123456) # set seed for reproducibility
lm_fit <- lm(response~.-feed_fraction_20, data=train_fractions) # simple linear regression
formula <- as.formula(step(lm_fit, k=log(nrow(train_fractions)), trace=0)) # BIC stepwise selection
lm_chosen <- lm(formula, data=train_fractions) # refit the chosen model
lm_coef <- data.frame(cbind(summary(lm_chosen)$coef, confint(lm_chosen)))
lm_coef <- lm_coef[order(lm_coef$Pr...t..),]
names(lm_coef) <- c("Estimate", "Std Error", "t-Stat", 
                    "p-value", "Lower 95% CI", "Upper 95% CI")
pred <- predict(lm_chosen, newdata = test_fractions) # generate prediction on test set
lm_rmse <- sqrt(mean((pred-test_fractions$response)^2)) # calculate metric RMSE
knitr::kable(round(cbind(lm_coef[1:8,1:3], lm_coef[1:8,5:6]), 4), 
             caption="Predictors with Strongest Effects")
par(mfrow=c(1, 2))
plot(lm_chosen, c(1, 2)) # diagnostic plots 

## lasso regression
library(glmnet) # load the lasso regression fitting function library
train_predictors <- model.matrix(as.formula(lm_fit), train_fractions) # create model matrix for train
test_predictors <- model.matrix(as.formula(lm_fit), test_fractions) # create model matrix for test
response <- train_fractions$response # extract response

set.seed(123456) # set seed for reproducibility
fit_lasso <- glmnet(train_predictors, response, alpha = 1) # fit a lasso on the train set
plot(fit_lasso, xvar = "lambda") # show shrinkage plot

grid <- exp(seq(-4, -11, length.out = 100)) # generate lambda grid to choose for CV
lasso_cv <- cv.glmnet(x=train_predictors, y=response, alpha=1, lambda=grid) # run CV with lasso

lambda_min <- lasso_cv$lambda.min # extract the optimum lambda
lasso_pred <- predict(lasso_cv, s = lasso_cv$lambda.min, newx = test_predictors) # get lasso predictions
lasso_rmse <- sqrt(mean((lasso_pred - test_fractions$response)^2)) # compute RMSE

lasso_coefs <- predict(fit_lasso, s = lasso_cv$lambda.min, type = "coefficients")
beta_lasso <- coef.glmnet(fit_lasso, s = lasso_cv$lambda.min)
hist(as.numeric(beta_lasso), breaks = 40, xlab = "coefficient") # make a histogram of the coefficients in the best model

## random forest regression
library(randomForest) # package for function that fits a random forest
set.seed(123456) # set seed for reproducibility
rf_fit <- randomForest(
  response ~ . - feed_fraction_20, 
  data = train_fractions, 
  mtry = 7, # m = max(floor(p/3), 1) = 7
  importance = TRUE)

rf_pred <- predict(object = rf_fit, newdata = test_fractions, type = "response") # predict on test set
rf_rmse <- sqrt(mean((rf_pred - test_fractions$response)^2)) # compute RMSE
rf_imp <- importance(rf_fit)
varImpPlot(rf_fit)

## trees boosting
library(gbm)
set.seed(123456)
bt_fit_cv1 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.01,
  interaction.depth = 3, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter1 <- gbm.perf(bt_fit_cv1, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred1 <- predict(bt_fit_cv1, newdata = test_fractions, 
                    n.trees = best_iter1, type = "response") # predict on test set
bt_rmse1 <- sqrt(mean((bt_pred1 - test_fractions$response)^2)) # compute RMSE

bt_fit_cv2 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.01,
  interaction.depth = 5, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter2 <- gbm.perf(bt_fit_cv2, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred2 <- predict(bt_fit_cv2, newdata = test_fractions, 
                    n.trees = best_iter2, type = "response") # predict on test set
bt_rmse2 <- sqrt(mean((bt_pred2 - test_fractions$response)^2)) # compute RMSE

bt_fit_cv3 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.05,
  interaction.depth = 3, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter3 <- gbm.perf(bt_fit_cv3, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred3 <- predict(bt_fit_cv3, newdata = test_fractions, 
                    n.trees = best_iter3, type = "response") # predict on test set
bt_rmse3 <- sqrt(mean((bt_pred3 - test_fractions$response)^2)) # compute RMSE

bt_fit_cv4 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.05,
  interaction.depth = 5, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter4 <- gbm.perf(bt_fit_cv4, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred4 <- predict(bt_fit_cv4, newdata = test_fractions, 
                    n.trees = best_iter4, type = "response") # predict on test set
bt_rmse4 <- sqrt(mean((bt_pred4 - test_fractions$response)^2)) # compute RMSE

bt_fit_cv5 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.1,
  interaction.depth = 3, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter5 <- gbm.perf(bt_fit_cv5, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred5 <- predict(bt_fit_cv5, newdata = test_fractions, 
                    n.trees = best_iter5, type = "response") # predict on test set
bt_rmse5 <- sqrt(mean((bt_pred5 - test_fractions$response)^2)) # compute RMSE

bt_fit_cv6 <- gbm(
  formula = response ~ . - feed_fraction_20,
  data = train_fractions,
  distribution = "gaussian", 
  n.trees = 1000, 
  shrinkage = 0.1,
  interaction.depth = 5, 
  cv.folds = 10) # fit a boosting regression tree model
best_iter6 <- gbm.perf(bt_fit_cv6, method = "cv", plot.it = FALSE) # extract optimal B
bt_pred6 <- predict(bt_fit_cv6, newdata = test_fractions, 
                    n.trees = best_iter6, type = "response") # predict on test set
bt_rmse6 <- sqrt(mean((bt_pred6 - test_fractions$response)^2)) # compute RMSE

bt_res <- data.frame(lambda=c(0.01, 0.01, 0.05, 0.05, 0.1, 0.1), 
                     d=c(3, 5, 3, 5, 3, 5), 
                     B=c(best_iter1, best_iter2, best_iter3, best_iter4, 
                         best_iter5, best_iter6),
                     RMSE=c(bt_rmse1, bt_rmse2, bt_rmse3, 
                            bt_rmse4, bt_rmse5, bt_rmse6)) # construct boosting result table
knitr::kable(bt_res, caption="Boosting Tuning Parameters and Results")

## finally, construct model comparison table
rmse <- c(lm_rmse, lasso_rmse, rf_rmse, bt_rmse3)
models <- c("Linear Regression", "Lasso Regression", "Random Forest", "Tree Boosting")
res_models <- data.frame("Model"=models, "RMSE"=round(rmse, 4))
knitr::kable(res_models, caption="RMSE for Model Comparison")

