function addGraphStat(stat) {
    console.log(stat);
    var contianer = document.getElementById("stats-container");
    var template = Handlebars.compile(document.getElementById("graph-stat-template").innerHTML);
    contianer.innerHTML += template({stat: stat});
}

function addTagStat(stat) {
    var contianer = document.getElementById("stats-container");
    var template = Handlebars.compile(document.getElementById("tag-stat-template").innerHTML);
    contianer.innerHTML += template({stat: stat});
}

var charts_drawn = 0;

function drawGraphStat(stat) {

    var chart_colors_faded = [
        'rgba(255, 99, 132, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(255, 205, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(153, 102, 255, 0.2)',
    ]

    var chart_colors_solid = [
        'rgba(255, 99, 132, 1)',
        'rgba(255, 159, 64, 1)',
        'rgba(255, 205, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(153, 102, 255, 1)',
    ]

    var ctx = document.getElementById(stat.name+'-graph').getContext('2d');
    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: stat.x,
            datasets: [{
                label: stat.name,
                data: stat.y,
                borderWidth: 1,
                borderColor: chart_colors_solid[charts_drawn],
                backgroundColor: chart_colors_faded[charts_drawn],
                tension: 0.3,
                fill: 'origin'
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                },
                x: {
                    ticks: {
                        callback: function(_value, index, _values) {
                            var max_len = 10;
                            var value = stat.x[index];
                            if (value.length > max_len) {
                                return value.substring(0, max_len-3) + "...";
                            }
                            return value;
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
            },
            responsive: true,
            maintainAspectRatio: false
        }
    });
    charts_drawn++;
    charts_drawn %= chart_colors_faded.length;
}

function loadStats() {
    document.getElementById("stats-container").innerHTML = "";
    // company or admin
    $.ajax({
        type: "GET",
        url: "/api/stats",
        success: function (response) {
            // build html
            response.forEach(stat => {
                switch (stat.type) {
                    case 'graph':
                        addGraphStat(stat);
                        break;
                    
                    case 'tag':
                        addTagStat(stat);
                        break;

                    default:
                        console.log("Unknown stat type: "+stat.type)
                        break;
                } 
            });
            // draw
            response.forEach(stat => {
                switch (stat.type) {
                    case 'graph':
                        drawGraphStat(stat);
                        break;

                    default:
                        break;
                } 
            });

            var msnry = new Masonry( '#stats-container', {
                itemSelector: '.stat',
                columnWidth: '.stat',
                percentPosition: true
            });

            msnry.layout();
            document.getElementById("stats-loading").style.display = "none";
        },
        error: function (xhr, status, error) {
            alert("Error loading stats: " + xhr.status + " " + xhr.statusText);
        }
    });
}