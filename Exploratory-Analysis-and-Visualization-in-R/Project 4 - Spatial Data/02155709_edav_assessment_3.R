library(tidyverse)
library(spData)
library(spdep)
library(gstat)
library(maps)

### Q1a.
BostonData <- boston.c %>%
  select(TOWN, LON, LAT, CMEDV) # select only required columns
ggplot(data=BostonData, aes(x=LON, y=LAT)) +
  geom_point(aes(col=TOWN)) + # plot town location by LON and LAT
  ggtitle("Observations in Boston Towns by Coordinates") + 
  coord_equal(ylim=c(42.0, 42.7)) + theme_bw() + 
  scale_size(range=c(0.5, 3.5)) + theme_minimal() + 
  theme(legend.position="none") + 
  labs(x="Longitude", y="Lattitude")
  
### Q1b.
boston.tc <- read.csv("~/Desktop/BostonTownCentres.csv")
names(boston.tc) <- c("TOWN", "LAT_CTR", "LON_CTR")
boston.join <- left_join(boston.tc, BostonData, by="TOWN")
ggplot(data=boston.join, aes(x=LON_CTR, y=LAT_CTR)) +
  geom_point(aes(col=TOWN)) + # plot town location by LON and LAT
  ggtitle("Boston Town Centers by Coordinates") + 
  coord_equal(ylim=c(42.0, 42.7)) + theme_bw() + 
  scale_size(range=c(0.5, 3.5)) + theme_minimal() + 
  theme(legend.position="none") + 
  labs(x="Longitude", y="Latitude")

### Q1c.
boston.join$LON_C <- numeric(nrow(boston.join))
boston.join$LAT_C <- numeric(nrow(boston.join))
for (i in unique(boston.join$TOWN)) {
  wrong_centroid <- c(mean(boston.join[boston.join$TOWN==i,]$LAT), 
                      mean(boston.join[boston.join$TOWN==i,]$LON)) # get mean of lon and lat
  true_centroid <- c(mean(boston.join[boston.join$TOWN==i,]$LAT_CTR), 
                     mean(boston.join[boston.join$TOWN==i,]$LON_CTR)) # get true town centers
  
  displacement <- true_centroid - wrong_centroid # compute displacement
  # add the displacement back to match true centroid
  boston.join[boston.join$TOWN==i,]$LAT_C <- 
    boston.join[boston.join$TOWN==i,]$LAT + displacement[1]
  boston.join[boston.join$TOWN==i,]$LON_C <- 
    boston.join[boston.join$TOWN==i,]$LON + displacement[2]
}


### Q1d.
ggplot(data=boston.join, aes(x=LON_C, y=LAT_C)) +
  geom_point(aes(col=CMEDV)) + # plot town location by LON and LAT+ 
  coord_equal(ylim=c(42.0, 42.7)) + theme_bw() + 
  scale_size(range=c(0.5, 3.5)) + theme_minimal() + 
  theme(legend.position="right") + 
  labs(
    x="Longitude", y="Latitude", title="Town Houses Median Value by Coordinates"
  )


## Q2. the data set is us_states_df from spData package
data("us_states_df")
states_map <- map_data("state") # get map data
us_states_df$`Median Income` <- us_states_df$median_income_10
states_data <- as.data.frame(us_states_df)
states_data$region <- tolower(states_data$state) # match column name
fact_join <- left_join(states_map, states_data, by="region") # join data set for a single plot
ggplot(fact_join, aes(x=long, y=lat, group=group)) + 
  geom_polygon(aes(fill=`Median Income`), color="white") + 
  labs(
    x="Longitude", y="Lattitude", 
    title="Median Income of 2010 by States"
  )