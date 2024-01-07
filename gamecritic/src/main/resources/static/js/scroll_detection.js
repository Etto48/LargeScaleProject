function canSee(target_id) {
    var dummy_loading_game = document.getElementById(target_id);
    var rect = dummy_loading_game.getBoundingClientRect();
    var current_scroll = window.innerHeight;
    return rect.top < current_scroll
}