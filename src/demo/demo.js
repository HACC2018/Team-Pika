    //var labels = jsonfile.sheet.map(function(e) { return e.date;});
    //var data = jsonfile.sheet.map(function(e) { return e.kw;});

    $(function() {
        $('input[name="daterange"]').daterangepicker({
          opens: 'left',
          format: 'M/DD hh:mm A'
        },
        function(start, end, label) {
          console.log("A new date selection was made: " + start.format('YYYY-MM-DDThh:mm:ss') + ' to ' + end.format('YYYY-MM-DDThh:mm:ss'));
          var url = start.format('YYYY-MM-DDThh:mm:ss') + '/' + end.format('YYYY-MM-DDThh:mm:ss');
  
  
      demo = {
        initPickColor: function() {
          $('.pick-class-label').click(function() {
            var new_class = $(this).attr('new-class');
            var old_class = $('#display-buttons').attr('data-class');
            var display_div = $('#display-buttons');
            if (display_div.length) {
              var display_buttons = display_div.find('.btn');
              display_buttons.removeClass(old_class);
              display_buttons.addClass(new_class);
              display_div.attr('data-class', new_class);
            }
          });
        },
  
        initDocChart: function() {
          chartColor = "#FFFFFF";
  
          // General configuration for the charts with Line gradientStroke
          gradientChartOptionsConfiguration = {
            maintainAspectRatio: false,
            legend: {
              display: false
            },
            tooltips: {
              bodySpacing: 4,
              mode: "nearest",
              intersect: 0,
              position: "nearest",
              xPadding: 10,
              yPadding: 10,
              caretPadding: 10
            },
            responsive: true,
            scales: {
              yAxes: [{
                display: 0,
                gridLines: 0,
                ticks: {
                  display: false
                },
                gridLines: {
                  zeroLineColor: "transparent",
                  drawTicks: false,
                  display: false,
                  drawBorder: false
                }
              }],
              xAxes: [{
                display: 0,
                gridLines: 0,
                ticks: {
                  display: false
                },
                gridLines: {
                  zeroLineColor: "transparent",
                  drawTicks: false,
                  display: false,
                  drawBorder: false
                }
              }]
            },
            layout: {
              padding: {
                left: 0,
                right: 0,
                top: 15,
                bottom: 15
              }
            }
          };
  
      myChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: "Active Users",
            fill: true,
            backgroundColor: gradientFill,
            borderWidth: 2,
            data: data
          }]
        },
        options: gradientChartOptionsConfiguration
      });
    },
  
    initDashboardPageCharts: function() {
  
      chartColor = "#FFFFFF";
  
      // General configuration for the charts with Line gradientStroke
      gradientChartOptionsConfiguration = {
        maintainAspectRatio: false,
        legend: {
          display: false
        },
        tooltips: {
          bodySpacing: 4,
          mode: "nearest",
          intersect: 0,
          position: "nearest",
          xPadding: 10,
          yPadding: 10,
          caretPadding: 10
        },
        responsive: 1,
        scales: {
          yAxes: [{
            display: 0,
            gridLines: 0,
            ticks: {
              display: false
            },
            gridLines: {
              zeroLineColor: "transparent",
              drawTicks: false,
              display: false,
              drawBorder: false
            }
          }],
          xAxes: [{
            display: 0,
            gridLines: 0,
            ticks: {
              display: false
            },
            gridLines: {
              zeroLineColor: "transparent",
              drawTicks: false,
              display: false,
              drawBorder: false
            }
          }]
        },
        layout: {
          padding: {
            left: 0,
            right: 0,
            top: 15,
            bottom: 15
          }
        }
      };
  
      gradientChartOptionsConfigurationWithNumbersAndGrid = {
        maintainAspectRatio: false,
        legend: {
          display: false
        },
        tooltips: {
          bodySpacing: 4,
          mode: "nearest",
          intersect: 0,
          position: "nearest",
          xPadding: 10,
          yPadding: 10,
          caretPadding: 10
        },
        responsive: true,
        scales: {
          yAxes: [{
            gridLines: 0,
            gridLines: {
              zeroLineColor: "transparent",
              drawBorder: false
            }
          }],
          xAxes: [{
            display: 0,
            gridLines: 0,
            ticks: {
              display: false
            },
            gridLines: {
              zeroLineColor: "transparent",
              drawTicks: false,
              display: false,
              drawBorder: false
            }
          }]
        },
        layout: {
          padding: {
            left: 0,
            right: 0,
            top: 15,
            bottom: 15
          }
        }
      };
  
      var ctx = document.getElementById('bigDashboardChart').getContext("2d");
  
      var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
      gradientStroke.addColorStop(0, '#80b6f4');
      gradientStroke.addColorStop(1, chartColor);
  
      var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
      gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
      gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");
  
      // url here
      $.getJSON("LOL3.json", function (result) {
          var labels = [], data = [];
          var week_labels = [];
          var week_data = [];
          var sum = 0;
          for (var i = 1; i < result.length ; i++){
                //
                sum += Math.round(result[i][column_name]);
                // From hours -> weekly data points
                data.push(Math.round(result[i].kW_AG_ENGINEERING_MAIN_MTR));
                labels.push(result[i].Timestamp);
                if (i % 168 == 0){
                  week_labels.push(result[i].Timestamp);
                  week_data.push(sum);
                  sum = 0;
                }
          }
  
      var myChart = new Chart(ctx, {
        type: 'line',
        data: {
        labels: week_labels,
          datasets: [{
            label: "Data",
            borderColor: chartColor,
            pointBorderColor: chartColor,
            pointBackgroundColor: "#1e3d60",
            pointHoverBackgroundColor: "#1e3d60",
            pointHoverBorderColor: chartColor,
            pointBorderWidth: 1,
            pointHoverRadius: 7,
            pointHoverBorderWidth: 2,
            pointRadius: 5,
            fill: true,
            backgroundColor: gradientFill,
            borderWidth: 2,
            data: week_data
          }]
        },
        options: {
          layout: {
            padding: {
              left: 20,
              right: 20,
              top: 0,
              bottom: 0
            },
          },
          maintainAspectRatio: false,
          tooltips: {
            backgroundColor: '#fff',
            titleFontColor: '#333',
            bodyFontColor: '#666',
            bodySpacing: 4,
            xPadding: 12,
            mode: "nearest",
            intersect: 0,
            position: "nearest"
          },
          legend: {
            position: "bottom",
            fillStyle: "#FFF",
            display: false
          },
          scales: {
            yAxes: [{
              ticks: {
                fontColor: "rgba(255,255,255,0.4)",
                fontStyle: "bold",
                beginAtZero: false,
                maxTicksLimit: 5,
                padding: 10
              },
              gridLines: {
                drawTicks: true,
                drawBorder: false,
                display: true,
                color: "rgba(255,255,255,0.1)",
                zeroLineColor: "transparent"
              }
  
            }],
            xAxes: [{
              gridLines: {
                zeroLineColor: "transparent",
                display: false,
                distribution: 'series'
              },
              ticks: {
                padding: 10,
                fontColor: "rgba(255,255,255,0.4)",
                fontStyle: "bold"
              }
            }]
          }
        }
      });
  
      $("#hour").click(function() {
      var chartStuff = myChart.config.data;
      chartStuff.datasets[0].data = data;
      chartStuff.labels = labels;
      myChart.update();
      });
  
      $("#week").click(function() {
      var week_chart = myChart.config.data;
      week_chart.datasets[0].data = week_data;
      week_chart.labels = week_labels;
      myChart.update();
      });
  });
  
  var cardStatsMiniLineColor = "#fff";
  var cardStatsMiniDotColor = "#fff";
  var bigChart = new Chart(ctx);
  
      var e = document.getElementById("barChartSimpleGradientsNumbers").getContext("2d");
  
      gradientFill = ctx.createLinearGradient(0, 170, 0, 50);
      gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
      gradientFill.addColorStop(1, hexToRGB('#2CA8FF', 0.6));
  
      var a = {
        type: "bar",
        data: {
          labels: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
          datasets: [{
            label: "Active Countries",
            backgroundColor: gradientFill,
            borderColor: "#2CA8FF",
            pointBorderColor: "#FFF",
            pointBackgroundColor: "#2CA8FF",
            pointBorderWidth: 2,
            pointHoverRadius: 4,
            pointHoverBorderWidth: 1,
            pointRadius: 4,
            fill: true,
            borderWidth: 1,
            data: [80, 99, 86, 96, 123, 85, 100, 75, 88, 90, 123, 155]
          }]
        },
        options: {
          maintainAspectRatio: false,
          legend: {
            display: false
          },
          tooltips: {
            bodySpacing: 4,
            mode: "nearest",
            intersect: 0,
            position: "nearest",
            xPadding: 10,
            yPadding: 10,
            caretPadding: 10
          },
          responsive: 1,
          scales: {
            yAxes: [{
              gridLines: 0,
              gridLines: {
                zeroLineColor: "transparent",
                drawBorder: false
              }
            }],
            xAxes: [{
              display: 0,
              gridLines: 0,
              ticks: {
                display: false
              },
              gridLines: {
                zeroLineColor: "transparent",
                drawTicks: false,
                display: false,
                drawBorder: false
              }
            }]
          },
          layout: {
            padding: {
              left: 0,
              right: 0,
              top: 15,
              bottom: 15
            }
          }
        }
      };
  
      var viewsChart = new Chart(e, a);
    },
  };
  });
  });
  