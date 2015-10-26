angular.module('klaudiabis', ['ui.materialize', 'pascalprecht.translate', 'languageModule'])
    .controller('AppCtrl', function HomeCtrl($scope) {
//        $state.go('home');
//        $scope.$state = $state;
//        Analytics.trackPage('/');

        $scope.menu = [
            {
                link: '#products',
                title: 'HOME.PRODUCTS',
                icon: 'dashboard'
            },
            {
                link: '#about',
                title: 'HOME.ABOUT',
                icon: 'group'
            },
            {
                link: '#contact',
                title: 'HOME.CONTACT',
                icon: 'message'
            }
        ];
        $scope.admin = [
            {
                link: '',
                title: 'Trash',
                icon: 'delete'
            },
            {
                link: 'showListBottomSheet($event)',
                title: 'Settings',
                icon: 'settings'
            }
        ];

        $scope.topProductsPairs = [
            [
                {
                    name: 'Ceratka',
                    year: 2014,
                    thumbnailUrl: 'http://klaudiabis.pl/assets/img/sample/ceratka1.jpg'
                },
                {
                    name: 'Tunika',
                    year: 2014,
                    thumbnailUrl: 'http://klaudiabis.pl/assets/img/sample/ceratka1.jpg'
                }
            ],
            [
                {
                    name: 'Armania',
                    year: 2014,
                    thumbnailUrl: 'http://klaudiabis.pl/assets/img/sample/ceratka1.jpg'
                },
                {
                    name: 'Trzy guziki',
                    year: 2015,
                    thumbnailUrl: 'http://klaudiabis.pl/assets/img/sample/ceratka1.jpg'
                }
            ]
        ];
    });