function showDropdown()
{
    var dropdown = document.getElementById('user-dropdown');
    dropdown.classList.toggle('show');
    dropdown.style.width = dropdown.parentElement.offsetWidth + "px";
}
function hideDropdown()
{
    document.getElementById('user-dropdown').classList.remove('show');
}