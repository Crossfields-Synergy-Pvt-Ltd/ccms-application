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

        $rootScope.previlage = {
            dist: 'ALL',
            mondal: 'ALL',
            gp: 'ALL',
            monitor_and_controller: true,
            history: true,
            event: true
        };

        $httpBackend.whenGET('/CCMS/dashboard/count?distrtict=ALL&mandal=ALL&gp=ALL')
            .respond({ total_devices: 100, light_on: 80, light_off: 20 });
        $httpBackend.whenGET('/CCMS/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL')
            .respond([{ lat: '16.4792', lang: '80.5469', light_status: 1 }]);
        $httpBackend.whenGET('/CCMS/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL')
            .respond([{ name: 'DCU-001', id: 'dcu1' }]);
        $httpBackend.whenGET('/CCMS/dashboard/instant_data_filter?distrtict=ALL&mandal=ALL&gp=ALL&page=1&size=10')
            .respond([]);
        $httpBackend.whenGET('/CCMS/filter/get_mandal?distrtict=ALL')
            .respond(['Tenali', 'Guntur Rural']);
        $httpBackend.whenGET('/CCMS/filter/get_gp?mandal=ALL')
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

        it('should set initial distric from previlage', function() {
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            expect($scope.distric).toBe('ALL');
        });

        it('should handle missing previlage gracefully', function() {
            $rootScope.previlage = null;
            $controller('dashboardControllers', {
                $scope: $scope, $rootScope: $rootScope,
                dashboardFactory: dashboardFactory, config: mockConfig,
                $state: mockState,
                $stateParams: mockStateParams,
                inform: mockInform,
                $modal: mockModal
            });
            $httpBackend.flush();
            expect($scope.distric).toBe('ALL');
            expect($scope.mondal).toBe('ALL');
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
            $scope.selecte_distict = 'ALL';
            $scope.select_mondal = 'ALL';
            $scope.select_gp = 'ALL';

            $httpBackend.whenGET('/CCMS/dashboard/count?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond({});
            $httpBackend.whenGET('/CCMS/dashboard/map_data?distrtict=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBe('?distrtict=ALL&mandal=ALL&gp=ALL');
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
            $scope.selecte_distict = 'Guntur-17';
            $scope.select_mondal = 'Tenali';
            $scope.select_gp = 'GP1';

            $httpBackend.whenGET('/CCMS/dashboard/count?distrtict=Guntur-17&mandal=Tenali&gp=GP1')
                .respond({});
            $httpBackend.whenGET('/CCMS/dashboard/map_data?distrtict=Guntur-17&mandal=Tenali&gp=GP1')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBe('?distrtict=Guntur-17&mandal=Tenali&gp=GP1');
        });
    });
});
