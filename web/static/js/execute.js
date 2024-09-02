document.getElementById('toggleSearchCheckBox').addEventListener('change', function () {
    var optionsDiv = document.getElementById('optionsDiv');
    if (this.checked) {
        optionsDiv.classList.remove('disabled-div');
        optionsDiv.classList.add('enabled-div');
    } else {
        optionsDiv.classList.remove('enabled-div');
        optionsDiv.classList.add('disabled-div');
    }
});

function showBlastPanel(check) {
    var blastInputs = document.getElementById("blast-inputs");
    console.log("mudou de valor " + check.checked);
    if (check.checked) {
        blastInputs.style.display = "block";
    } else {
        blastInputs.style.display = "none";
    }
}