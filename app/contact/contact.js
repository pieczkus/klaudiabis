angular.module('contactModule', ['ui.router'])
    .controller('contactCtrl', function ($scope, $state, Analytics) {
        Analytics.trackPage('/contact');
    });