$(document).ready(function () {
    var table = $('#dataTable').DataTable({
        paging: false,   // Desativa a paginação
        scrollY: "40vh", // Define a altura da rolagem vertical para 70% da altura da tela
        scrollCollapse: true, // Permite que a tabela reduza a altura caso tenha poucos dados
        scrollX: true,
        info: true

    });

    function plotScatterPlot() {
        var filteredData = table.rows({filter: 'applied'}).data().toArray();
        var x = [];
        var y = [];

        // Preenche os arrays x e y com base nos dados filtrados
        filteredData.forEach(row => {
            x.push(parseFloat(row[4])); // ponto_isoeletrico
            y.push(parseFloat(row[3])); // kda
        });

        var trace1 = {
            x: x,
            y: y,
            type: 'histogram2dcontour',
            colorbar: {
                title: 'Density'
            },
            colorscale: [
                [0, 'rgb(255,255,255)'],    // Branco
                [0.2, 'rgb(200,220,255)'],   // Azul claro
                [0.4, 'rgb(150,190,255)'],   // Azul médio claro
                [0.6, 'rgb(100,150,255)'],   // Azul médio
                [0.8, 'rgb(50,100,255)'],    // Azul mais escuro
                [1, 'rgb(0,50,255)']         // Azul escuro
            ],
            reversescale: false
        };

        var trace2 = {
            x: x,
            y: y,
            mode: 'markers',
            type: 'scatter',
            marker: {
                size: 10,
                color: 'rgba(17, 157, 255, 0.7)',
                line: {
                    width: 1,
                    color: 'rgba(17, 157, 255, 0.9)'
                }
            }
        };

        var layout = {
            xaxis: {
                title: 'Isoelectric Point (p.H)',
                range: [0, 14]
            },
            yaxis: {
                title: 'Molecular Massa (kDa)',
                rangemode: 'tozero'
            },
            title: 'Molecular mass (kDa) vs Isoelectric Point (p.H)',
            paper_bgcolor: '#e0e0e0',
            plot_bgcolor: '#ffffff'
        };

        Plotly.newPlot('scatterPlot', [trace1, trace2], layout);
    }

    // Atualizar gráfico ao aplicar filtros
    table.on('draw', plotScatterPlot);

    function generateChart() {
        // Coletando os dados filtrados da coluna 'subcellular_localization'
        var data = table.column(5, {search: 'applied'}).data().toArray();

        // Contando as ocorrências de cada categoria
        var counts = {};
        data.forEach(function (value) {
            counts[value] = (counts[value] || 0) + 1;
        });

        // Convertendo os dados para Chart.js
        var labels = Object.keys(counts);
        var chartData = Object.values(counts);

        // Destruindo o gráfico anterior, se houver
        if (window.myPieChart) {
            window.myPieChart.destroy();
        }

        var backgroundColors = [
            'rgba(173, 216, 230, 0.6)', // LightBlue
            'rgba(135, 206, 250, 0.6)', // SkyBlue
            'rgba(70, 130, 180, 0.6)',  // SteelBlue
            'rgba(0, 0, 255, 0.6)',     // Blue
            'rgba(0, 0, 139, 0.6)',     // DarkBlue
            'rgba(25, 25, 112, 0.6)'    // MidnightBlue
        ];

        var borderColors = [
            'rgba(173, 216, 230, 1)', // LightBlue
            'rgba(135, 206, 250, 1)', // SkyBlue
            'rgba(70, 130, 180, 1)',  // SteelBlue
            'rgba(0, 0, 255, 1)',     // Blue
            'rgba(0, 0, 139, 1)',     // DarkBlue
            'rgba(25, 25, 112, 1)'    // MidnightBlue
        ];

        // Gerando o novo gráfico de pizza
        var ctx = document.getElementById('myChart').getContext('2d');
        window.myPieChart = new Chart(ctx, {
            height: 400,
            width: 500,
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Occurrences',
                    data: chartData,
                    backgroundColor: backgroundColors.slice(0, labels.length),
                    borderColor: borderColors.slice(0, labels.length),
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'right',
                    },
                    title: {
                        display: true,   // Exibe o título
                        text: 'Subcellular Localization Occurrences',  // Texto do título
                        font: {
                            size: 18      // Tamanho da fonte do título
                        }
                    }
                },
                responsive: true,
                maintainAspectRatio: true

            }
        });
    }

    function setTextFilter(idx, filtername) {
        $(filtername).on('keyup', function () {
            table.column(idx).search(this.value).draw();
            var filteredIds = [];
            table.rows({filter: 'applied'}).data().each(function (row) {
                filteredIds.push(row[0]); // Pegando o ID (primeira coluna)
            });
            $('#filteredIds').val(filteredIds.join(','));

            console.log($('#filteredIds').val())
            generateChart();
            plotScatterPlot();
        });
    }

    function setSelectFilter(idx, filterName) {
        table.column(idx).data().unique().sort().each(function (d, j) {
            $(filterName).append('<option value="' + d + '">' + d + '</option>');
        });
        $(filterName).on('change', function () {
            var selectedValues = $(this).val(); // Array com os valores selecionados
            if (selectedValues) {
                var regex = selectedValues.join('|');
                table.column(idx).search(regex, true, false).draw();
            } else {
                table.column(idx).search('').draw();
            }

            var filteredIds = [];
            table.rows({filter: 'applied'}).data().each(function (row) {
                filteredIds.push(row[0]); // Pegando o ID (primeira coluna)
            });
            $('#filteredIds').val(filteredIds.join(','));

            console.log($('#filteredIds').val())
            generateChart();
            plotScatterPlot();
        });
    }

    setTextFilter(0, "#IdFilter")
    setTextFilter(1, "#HeaderFilter")
    setTextFilter(2, "#SequenceFilter")
    setSelectFilter(5, "#SubcellularLocalizationFilter")
    setSelectFilter(6, "#TMFilter");
    setSelectFilter(7, "#PhobiusTMFilter");
    setSelectFilter(8, "#SignalPFilter");
    setSelectFilter(9, "#PhobiusSPFilter");
    setTextFilter(10, "#AlignmentFilter")
    setSelectFilter(11, "#GPIFilter");
    setSelectFilter(12, "#MembraneFilter");
    setTextFilter(13, "#MembraneEvidenceFilter")
    setSelectFilter(14, "#NGLYCFilter")
    setSelectFilter(15, "#ErretFilter")
    setTextFilter(16, "#PFAMFilter")
    setTextFilter(17, "#PANTHERFilter")
    setTextFilter(18, "#IPRIdFilter")
    setTextFilter(19, "#GOIdFilter")

    $('#clearFilters').on('click', function () {
        // Limpa os campos de texto
        $('#IdFilter').val('').trigger('keyup');
        $('#HeaderFilter').val('').trigger('keyup');
        $('#SequenceFilter').val('').trigger('keyup');
        $('#KdaFilter').val('').trigger('keyup');
        $('#IpFilter').val('').trigger('keyup');
        $('#SubcellularLocalizationFilter').val('').trigger('change');
        $('#TMFilter').val('').trigger('change');
        $('#PhobiusTMFilter').val('').trigger('change');
        $('#SignalPFilter').val('').trigger('change');
        $('#PhobiusSPFilter').val('').trigger('change');
        $('#AlignmentFilter').val('').trigger('keyup');
        $('#GPIFilter').val('').trigger('change');
        $('#MembraneFilter').val('').trigger('change');
        $('#MembraneEvidenceFilter').val('').trigger('keyup');
        $('#NGLYCFilter').val('').trigger('keyup');
        $('#ErretFilter').val('').trigger('keyup');
        $('#PFAMFilter').val('').trigger('keyup');
        $('#PANTHERFilter').val('').trigger('keyup');
        $('#IPRIdFilter').val('').trigger('keyup');
        $('#GOIdFilter').val('').trigger('keyup');

        table.search('').columns().search('').draw();

    });
    var canvas = document.getElementById('myChart');
    canvas.width = 50; // Largura desejada
    canvas.height = 50; // Altura desejada

    generateChart();
    plotScatterPlot();


    $('#downloadBtn').on('click', function () {
        // Cria um objeto FormData para enviar os dados
        var formData = new FormData();

        // Adiciona o conteúdo do input hidden
        formData.append('filteredIds', $('#filteredIds').val());
        formData.append('file', $('#fileName').val());

        console.log('Arquivo ' + $('#fileName').val())

        // Envia a requisição AJAX com os dados
        $.ajax({
            url: '/download-csv',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                // Cria um link temporário para iniciar o download
                var a = document.createElement('a');
                a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(data);
                a.download = 'proteins.csv';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
            }
        });
    });

    function handleDownload(fileExtension, fileName) {
        var formData = new FormData();
        formData.append('filteredIds', $('#filteredIds').val());
        formData.append('file', $('#fileName').val());
        formData.append('ext', fileExtension);

        var url = '/download'

        $.ajax({
            url: url,
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                var mimeType;
                switch (fileExtension) {
                    case 'csv':
                        mimeType = 'text/csv';
                        break;
                    case 'tsv':
                        mimeType = 'text/tsv';
                        break;
                    case 'fasta':
                        mimeType = 'text/plain';
                        break;
                    default:
                        mimeType = 'application/octet-stream';
                }

                var a = document.createElement('a');
                a.href = 'data:' + mimeType + ';charset=utf-8,' + encodeURIComponent(data);
                a.download = fileName;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
            }
        });
    }

    $('#downloadCsvBtn').on('click', function () {
        handleDownload('csv', 'proteins.csv');
    });

    $('#downloadTsvBtn').on('click', function () {
        handleDownload('tsv', 'proteins.tsv');
    });

    $('#downloadFastaBtn').on('click', function () {
        handleDownload('fasta', 'proteins.fasta');
    });
});
