angular.module('klaudiabis', ['ui.router', 'pascalprecht.translate', 'ngAnimate', 'navigationModule', 'languageModule'])
    .controller('AppCtrl', function HomeCtrl($scope, $state) {
        $state.go('home');
        $scope.$state = $state;
    });