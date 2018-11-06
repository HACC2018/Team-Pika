$(function() {
  $('input[name="daterange"]').daterangepicker({
    opens: 'left',
    xhrFields: { withCredentials: true }
  }, function(start, end, label) {
    console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    var url = start.format('YYYY-MM-DDThh:mm:ss') + '/' + end.format('YYYY-MM-DDThh:mm:ss');
    console.log("MEOW");

    var ctx = document.getElementById('bigDashboardChart').getContext("2d");

    var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
    gradientStroke.addColorStop(0, '#80b6f4');

    var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");

    var all = "https://t6snn1lxw3.execute-api.us-west-2.amazonaws.com/default/DataS3GET/" + url;

    console.log(all);
    console.log("HELLO");
    $.getJSON(all, function (result) {
        var labels = [], data = [];
        var week_labels = [];
        var week_data = [];
        var sum = 0;
        for (var i = 0; i < result.length ; i++){
              //sum += Math.round(result[i][column_name]);
              // From hours -> weekly data points
                data.push(Math.round(result[i].kW_AG_ENGINEERING_MAIN_MTR));
                labels.push(result[i].Timestamp);


        }

    var myChart = new Chart(ctx, {
      type: 'line',
      data: {
      labels: labels,
        datasets: [{
          label: "Data",
        //  borderColor: chartColor,
        //  pointBorderColor: chartColor,
          pointBackgroundColor: "#1e3d60",
          pointHoverBackgroundColor: "#1e3d60",
        //  pointHoverBorderColor: chartColor,
          pointBorderWidth: 1,
          pointHoverRadius: 7,
          pointHoverBorderWidth: 2,
          pointRadius: 5,
          fill: true,
          backgroundColor: gradientFill,
          borderWidth: 2,
          data: data
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
});
  });
});
