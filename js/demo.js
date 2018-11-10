var building = [];

function addArrayPost(e) {
  if (e.target.checked) {
    addArr(e.target);
  } else {
    removeArr(e.target);
  }
}
var arr = [];

function addArr(obj) {
  arr.push(obj.value);
  console.log(arr);
}

function removeArr(obj) {
  var index = arr.indexOf(obj.value);
  if (index > -1) {
    arr.splice(index, 1);
  }
}


$(function () {
  $('input[name="daterange"]').daterangepicker({
    opens: 'left',
    xhrFields: {
      withCredentials: true
    }
  }, function (start, end, label) {
    console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    var url = start.format('YYYY-MM-DDThh:mm:ss') + '/' + end.format('YYYY-MM-DDThh:mm:ss');

    var ctx = document.getElementById('bigDashboardChart').getContext("2d");

    var gradientStroke = ctx.createLinearGradient(500, 0, 100, 0);
    gradientStroke.addColorStop(0, '#80b6f4');

    var gradientFill = ctx.createLinearGradient(0, 200, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(255, 255, 255, 0.24)");

    var all = "https://t6snn1lxw3.execute-api.us-west-2.amazonaws.com/default/DataS3GET/" + url;
    console.log(all);

    var myChart;

    $.getJSON(all, function (result) {
      var labels = [],
        data = [];
      var sum = 0;
      var totalSum = 0;
      console.log(result);

      for (var i = 0; i < result.length; i++) {
        //sum += Math.round(result[i][column_name[j]]]);
        // From hours -> weekly data points
        console.log(arr);
        for (var x = 0; x < arr.length; x++) {
          console.log(result[i][arr[x]]);
          sum += Math.round(result[i][arr[x]]);  
        }
        totalSum += sum;
        // 24 hours
        data.push(sum);
        labels.push(result[i].fulldatetime.split(" ")[0]);
        sum = 0;
      }
      console.log(data);

      // Operation SC
      $('#bigDashboardChart').remove();
      $('#bigDashboardWrapper').append('<canvas id="bigDashboardChart"></canvas>');
      ctx = $("#bigDashboardChart").get(0).getContext("2d");

      averageData = []
      maxData = []

      averageKwh = Math.round(totalSum / (data.length + 1) * 100) / 100;
      averageCO2 = Math.round(averageKwh * 0.35156 * 100) / 100;
      maxKwh = Math.max(...data);

      for (let i = 0; i < result.length; i++) {
        averageData.push(averageKwh);
        maxData.push(maxKwh);
      }

      myChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: "Power Consumption",
            borderColor: "#ffffff",
            //  pointBorderColor: chartColor,
            pointBackgroundColor: "#ffffff",
            pointHoverBackgroundColor: "#ffffff",
            //  pointHoverBorderColor: chartColor,
            pointBorderWidth: 0.5,
            pointHoverRadius: 7,
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            fill: true,
            backgroundColor: gradientFill,
            borderWidth: 2,
            data: data
          }, {
            label: "Average Power Consumption",
            borderColor: "#0fdc63",
            //  pointBorderColor: chartColor,
            pointBackgroundColor: "#0fdc63",
            pointHoverBackgroundColor: "#0fdc63",
            //  pointHoverBorderColor: chartColor,
            pointBorderWidth: 0.5,
            pointHoverRadius: 7,
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            // fill: true,
            // backgroundColor: gradientFill,
            borderWidth: 2,
            data: averageData,
          }, {
            label: "Max Power Consumption",
            borderColor: "#ff9900",
            //  pointBorderColor: chartColor,
            pointBackgroundColor: "#ff9900",
            pointHoverBackgroundColor: "#ff9900",
            //  pointHoverBorderColor: chartColor,
            pointBorderWidth: 0.5,
            pointHoverRadius: 7,
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            // fill: true,
            // backgroundColor: gradientFill,
            borderWidth: 2,
            data: maxData,
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
            display: true
          },
          scales: {
            yAxes: [{
              ticks: {
                fontColor: "rgba(255,255,255,0.4)",
                fontStyle: "bold",
                beginAtZero: false,
                maxTicksLimit: 5,
                padding: 10,
                display: true,
                labelString: 'Energy Consumption'
              },
              gridLines: {
                drawTicks: false,
                drawBorder: false,
                display: false,
                color: "rgba(255,255,255,0.1)",
                zeroLineColor: "transparent"
              }

            }],
            xAxes: [{
              gridLines: {
                zeroLineColor: "transparent",
                display: false,
                distribution: 'series',
                labelString: 'Date'
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

      ctx = document.getElementById("canvas2").getContext("2d");
      kWHGauge = new Chart(ctx, {
        type: "tsgauge",
        data: {
          datasets: [{
            backgroundColor: ["#41EADA", "#0fdc63", "#fd9704", "#ff7143"],
            borderWidth: 0,
            gaugeData: {
              value: averageKwh,
              valueColor: "#ff7143"
            },
            // Lowest, Avg, Max, Unreachable
            gaugeLimits: [0, 28, 56, 148, 250]
          }]
        },
        options: {
          events: []
        }
      });

      ctx = document.getElementById("canvas1").getContext("2d");
      carbonGauge = new Chart(ctx, {
        type: "tsgauge",
        data: {
          datasets: [{
            backgroundColor: ["#41EADA", "#0fdc63", "#fd9704", "#ff7143"],
            borderWidth: 0,
            gaugeData: {
              value: averageCO2,
              valueColor: "#ff7143"
            },
            gaugeLimits: [0, 10, 20, 52, 88]
          }]
        },
        options: {
          events: []
        }
      });
    });
  });
});
