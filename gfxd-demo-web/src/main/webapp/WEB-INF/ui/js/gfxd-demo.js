var tv = 1000;

var throughput = new Rickshaw.Graph({
    element: document.querySelector("#event_rate_chart"),
    width: "400",
    height: "200",
    renderer: "line",
    series: new Rickshaw.Series.FixedDuration([
        {
            name: 'one',
            color: 'gold'
        }
    ], undefined, {
        timeInterval: tv,
        maxDataPoints: 100,
        timeBase: new Date().getTime() / 1000
    })
});
var axes = new Rickshaw.Graph.Axis.Time({
    graph: throughput
});
axes.render();

var yAxis = new Rickshaw.Graph.Axis.Y({
    graph: throughput,
//        pixelsPerTick: 10,
//        tickSize: 1
});
yAxis.render();

throughput.render();

setInterval(function () {
    pollServer(throughput);
}, tv);

function pollServer(chart) {
    // Change this to "/data/events-loaded for real data
    d3.json("/data/random", function (error, json) {
        if (error) {
            console.log(error);
        } else {
            var data = {one: json.value};
            chart.series.addData(data);
            chart.render();
            yAxis.render();
            chartModel.eventRate(Math.round(json.value));
        }
    })
}

function ChartViewModel() {
    this.eventRate = ko.observable(0);
}

var chartModel = new ChartViewModel();

// Activates knockout.js
ko.applyBindings(chartModel);
