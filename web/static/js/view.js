$(document).ready(function () {
    var table = $('#dataTable').DataTable({
        paging: false,   // Desativa a paginação
        scrollY: "40vh", // Define a altura da rolagem vertical para 70% da altura da tela
        scrollCollapse: true, // Permite que a tabela reduza a altura caso tenha poucos dados
        scrollX: true,
        info: true

    });

    function setTextFilter(idx, filtername) {
        $(filtername).on('keyup', function () {
            table.column(idx).search(this.value).draw();
            var filteredIds = [];
            table.rows({filter: 'applied'}).data().each(function (row) {
                filteredIds.push(row[0]); // Pegando o ID (primeira coluna)
            });
            $('#filteredIds').val(filteredIds.join(','));

            console.log($('#filteredIds').val())

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

    $('#downloadBtn').on('click', function () {
        alert('Clicou')
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
                console.log(fileName)
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

function loadLogFile(url, title) {
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(text => {
            document.getElementById('fileTitle').textContent = title;
            document.getElementById('outputAreaModal').textContent = text;
            document.getElementById('downloadFile').href = url;
        })
        .catch(error => {
            document.getElementById('outputAreaModal').textContent = 'Failed to load file: ' + error.message;
        });
}