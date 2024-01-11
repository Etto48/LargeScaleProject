function addGraphStat(stat) {
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
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            },
            responsive: true,
            maintainAspectRatio: false
        }
    });
    charts_drawn++;
    charts_drawn %= chart_colors_faded.length;
}

function enableSetMinHeightGraphStats() {
    // this function fixes a bug that I can't even explain...
    // in a nutshell, the charts were growing when in the same row 
    // as another type of stat, but they were not shrinking back when they were alone
    // this hack kinda fixes it
    // I have not slept this night :(

    function setMinHeight() {
        var stats = $(".stat");
        var graph_stats_rows = [];
        var this_row = [];
        var last_top = null;
        var all_graph_stats = true;
        stats.each(function(index,stat) {
            var top = stat.getBoundingClientRect().top; 
            if(last_top == null)
            {
                last_top = top;
            }
            if(top == last_top)
            {
                this_row.push(stat);
                if(!stat.classList.contains("graph-stat"))
                {
                    all_graph_stats = false;
                }
            }
            else
            {
                if(all_graph_stats)
                {
                    graph_stats_rows.push(this_row);
                }
                this_row = [stat];
                last_top = top;
                all_graph_stats = stat.classList.contains("graph-stat");
            }
            if(index == stats.length - 1)
            {
                if(all_graph_stats)
                {
                    graph_stats_rows.push(this_row);
                }
            }
        });
        stats.each(function(index,stat) {
            $(stat).find(".stat-chart-container").css("height",null);
        });
        graph_stats_rows.forEach(row => {
            row.forEach(stat => {
                $(stat).find(".stat-chart-container").css("height","150px");
            });
        });
    }

    setMinHeight();
    window.addEventListener('resize', function() {
        setMinHeight();
    });
}

function loadStats(mode = 'company') {
    document.getElementById("stats-container").innerHTML = "";
    // company or admin
    $.ajax({
        type: "GET",
        url: "/api/"+mode+"/stats",
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

            enableSetMinHeightGraphStats();
        },
        error: function (xhr, status, error) {
            alert("Error loading stats: " + xhr.status + " " + xhr.statusText);
        }
    });
}