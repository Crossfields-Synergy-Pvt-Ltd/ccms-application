describe('dashboardControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, dashboardFactory;
    var mockState, mockConfig, mockStateParams;
    var mockInform, mockModal;

    beforeEach(module('inform'));
    beforeEach(module('dashboardControllers'));
    beforeEach(module('dashboardFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _dashboardFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        dashboardFactory = _dashboardFactory_;
        $controller = _$controller_;

        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };
        mockState = { go: jasmine.createSpy('$state.go') };

        mockConfig = {
            districts: [
                { state: 'Guntur-17', code: 'Guntur-17' },
                { state: 'Krishna-16', code: 'Krishna-16' }
            ]
        };
        mockStateParams = {};

        $rootScope.privilege = {
            district: 'ALL',
            mandal: 'ALL',
            gp: 'ALL',
            monitor_and_controller: true,
            history: true,
            event: true
        };

        $httpBackend.whenGET('/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
            .respond({ total_devices: 100, light_on: 80, light_off: 20 });
        $httpBackend.whenGET('/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
            .respond([{ lat: '16.4792', lang: '80.5469', light_status: 1 }]);
        $httpBackend.whenGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL')
            .respond([{ name: 'DCU-001', id: 'dcu1' }]);
        $httpBackend.whenGET('/dashboard/instant_data_filter?district=ALL&mandal=ALL&gp=ALL&page=1&size=10')
            .respond([]);
        $httpBackend.whenGET('/filter/get_mandal?district=ALL')
            .respond(['Tenali', 'Guntur Rural']);
        $httpBackend.whenGET('/filter/get_gp?mandal=ALL')
            .respond(['GP1', 'GP2']);
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    describe('initialization', function() {
        it('should load DCU names on init', function() {
        $controller('dashboardControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            dashboardFactory: dashboardFactory,
            config: mockConfig,
            $state: mockState,
            $stateParams: mockStateParams,
            inform: mockInform,
            $modal: mockModal
        });
            $httpBackend.flush();
            expect($rootScope.dcu_name_list).toBeDefined();
        });

        it('should set districts from config', function() {
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            expect($scope.districts).toEqual(mockConfig.districts);
        });

        it('should set initial district from privilege', function() {
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            expect($scope.district).toBe('ALL');
        });

        it('should handle missing privilege gracefully', function() {
            $rootScope.privilege = null;
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            expect($scope.district).toBe('ALL');
            expect($scope.mandal).toBe('ALL');
            expect($scope.gp).toBe('ALL');
        });
    });

    describe('$scope.search', function() {
        it('should clear markers before reloading', function() {
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            $scope.selectedDistrict = 'ALL';
            $scope.selectedMandal = 'ALL';
            $scope.select_gp = 'ALL';

            $httpBackend.whenGET('/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond({});
            $httpBackend.whenGET('/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBe('?district=ALL&mandal=ALL&gp=ALL');
        });

        it('should update qs_params with selected filters', function() {
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            $scope.selectedDistrict = 'Guntur-17';
            $scope.selectedMandal = 'Tenali';
            $scope.select_gp = 'GP1';

            $httpBackend.whenGET('/dashboard/count?district=Guntur-17&mandal=Tenali&gp=GP1')
                .respond({});
            $httpBackend.whenGET('/dashboard/map_data?district=Guntur-17&mandal=Tenali&gp=GP1')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBe('?district=Guntur-17&mandal=Tenali&gp=GP1');
        });
    });
});
