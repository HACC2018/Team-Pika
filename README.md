<h1 align="center">UH Manoa Energy Analytics</h1>

<div align="center">
	:zap:
</div>
<div align="center">
  <strong>Developed by Team Pika</strong>
</div>

<div align="center">
  <h3>
  	<a href="https://hacc2018.github.io/Team-Pika/">
      Website
    </a>
  	<span> | </span>
    <a href="https://devpost.com/software/uh-manoa-energy-analytics">
      Devpost
    </a>
  </h3>
</div>

<div align="center">
  <sub>Built with ❤ & Milk Tea 🍵
</div>

## Table of Contents
- [Overview](#overview)

## Philosophy
On June 8, 2015, Governor David Ige signed into law HRS 304a-119 which established a collective goal for the University of Hawai‘i “to become net-zero with respect to energy use, producing as much (renewable) energy as the system consumes across all campuses by January 1, 2035.”

The goal of this project is to assist the University of Hawaii Director of Energy Management and the State of Hawaii to achieve the requirements described by this new law.  In the first stage of preparation, the current energy consumption for the University of Hawaii will be determined.  The data will then be used as a baseline to help establish the definition upon which energy goals are created.  The University of Hawaii will then be able to calculate their progress, how much energy it will need to produce and reduce, on their way towards the Net Zero mandate.  

Energy consumption must be calculated to determine the progress toward the Net Zero goal.  Traditionally, energy consumption is defined by kilowatts usage per hour per square foot.  Unfortunately, this traditional method of calculation produces numbers that are more useful in determining a baseline comparison with residential and corporate buildings. This particular baseline does not work well within a university environment because they have a wide range of energy usage.  A classroom building will have a lower usage than the Stan Sheriff building on a game day.  The Duke Kahanamoku pool will have less of a power drop over a school break than the Campus Center.  As a university, a rising issue would be energy usage compared to the number of degrees or research dollars awarded. 

As expected, the energy requirements for the University of Hawaii are unique in nature.  In the community, energy is most often calculated in terms of Kilowatts per hour (kWh).  The University of Hawaii fails to compare when looking over at this statistic versus other industries or residential data.  To this end, the data analytics used in this project will help by defining a new baseline number.  We will be calculating the energy consumption per degrees offered annually and energy consumption per research dollar spend.  This data can then be used to compare with other universities to see the efficiency of the University of Hawaii.

## Overview

This dashboard will also help the University of Hawaii monitor and analyze their progress as they proceed with their various Net Zero Energy projects as a subset for the previous goals, such as the installation of Photovoltaic cells over the parking structure and various buildings, installation and modernization of various electrical equipment and lights, and the installation of various batteries to help offset usage.

The project minimizes the impact on energy by taking advantage of AWS Lambda Serverless Computing to scale and run only when computing resources are required. The data has been stored into aggregated variants on AWS S3 to explore various time intervals immediately upon request. This is accomplished by consuming the data using a REST API through AWS API Gateway. The visual exploration focuses on monitoring the energy levels directly through a dashboard and geographically using a 3D map.

For the front-end, we used Bootstrap for the dashboard design, Chartjs for our graph data visualizations, and mapbox for our 3D building visualization. 

## License
[MIT](https://tldrlegal.com/license/mit-license)
