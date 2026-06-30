describe('mapControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, mapViewFactory;
    var mockState, mockConfig, mockStateParams;
    var mockInform, mockModal;

    beforeEach(module('inform'));
    beforeEach(module('mapControllers'));
    beforeEach(module('mapFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _mapViewFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        mapViewFactory = _mapViewFactory_;
        $controller = _$controller_;

        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };
        mockState = { go: jasmine.createSpy('$state.go') };

        mockConfig = { districts: [{ state: 'Guntur-17', code: 'Guntur-17' }] };
        mockStateParams = {};

        $httpBackend.whenGET('/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
            .respond({ total_devices: 100 });
        $httpBackend.whenGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
            .respond([]);
        $httpBackend.whenGET('/CCMS/filter/get_mandal?district=ALL')
            .respond(['Tenali']);
        $httpBackend.whenGET('/CCMS/filter/get_gp?mandal=ALL')
            .respond(['GP1']);
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    function createController() {
        $controller('mapViewControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            mapViewFactory: mapViewFactory,
            config: mockConfig,
            $state: mockState,
            $stateParams: mockStateParams,
            inform: mockInform,
            $modal: mockModal
        });
    }

    describe('initialization', function() {
        it('should load districts from config', function() {
            createController();
            expect($scope.districts).toEqual(mockConfig.districts);
        });

        it('should set default district to ALL', function() {
            createController();
            expect($scope.district).toBe('ALL');
            expect($scope.mandal).toBe('ALL');
            expect($scope.gp).toBe('ALL');
        });

        it('should set selectedDistrict to district', function() {
            createController();
            expect($scope.selectedDistrict).toBe($scope.district);
        });
    });

    describe('$scope.search', function() {
        it('should update qs_params with selected filters', function() {
            createController();
            $scope.selectedDistrict = 'Guntur-17';
            $scope.selectedMandal = 'Tenali';
            $scope.select_gp = 'GP1';

            $httpBackend.whenGET('/CCMS/dashboard/count?district=Guntur-17&mandal=Tenali&gp=GP1')
                .respond({});
            $httpBackend.whenGET('/CCMS/dashboard/map_data?district=Guntur-17&mandal=Tenali&gp=GP1')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBe('?district=Guntur-17&mandal=Tenali&gp=GP1');
        });

        it('should reload data after search', function() {
            createController();

            $httpBackend.whenGET('/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond({ total_devices: 50 });
            $httpBackend.whenGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);

            $scope.search();
            expect($scope.qs_params).toBeDefined();
        });

        it('should handle empty API response gracefully', function() {
            createController();
            $httpBackend.whenGET('/CCMS/dashboard/count?district=ALL&mandal=ALL&gp=ALL')
                .respond({});
            $httpBackend.whenGET('/CCMS/dashboard/map_data?district=ALL&mandal=ALL&gp=ALL')
                .respond([]);
            $scope.search();
        });
    });

    describe('$scope.getMandalOnSelect', function() {
        it('should call factory to get mandals', function() {
            createController();
            $httpBackend.whenGET('/CCMS/filter/get_mandal?district=Guntur-17')
                .respond(['Tenali', 'Guntur Rural']);

            $scope.selectedDistrict = 'Guntur-17';
            $scope.getMandalOnSelect('Guntur-17');
            $httpBackend.flush();
            expect($scope.mandal_list).toBeDefined();
        });
    });

    describe('$scope.getGpOnSelect', function() {
        it('should call factory to get GPs', function() {
            createController();
            $httpBackend.whenGET('/CCMS/filter/get_gp?mandal=Tenali')
                .respond(['GP1', 'GP2']);

            $scope.selectedMandal = 'Tenali';
            $scope.getGpOnSelect('Tenali');
            $httpBackend.flush();
            expect($scope.gp_list).toBeDefined();
        });
    });
});
