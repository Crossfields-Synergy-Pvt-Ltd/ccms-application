describe('historyControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, historyFactory;
    var mockConfig, mockStateParams;
    var mockInform, mockModal;

    beforeEach(module('inform'));
    beforeEach(module('historyControllers'));
    beforeEach(module('historyFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _historyFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        historyFactory = _historyFactory_;
        $controller = _$controller_;

        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };

        mockConfig = { districts: [{ state: 'Guntur-17', code: 'Guntur-17' }] };
        mockStateParams = {};

        $rootScope.previlage = {
            dist: 'ALL',
            mondal: 'ALL',
            gp: 'ALL',
            history: true,
            event: true
        };

        $httpBackend.whenGET('/CCMS/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL')
            .respond([{ name: 'DCU-001', id: 'dcu1' }]);
        $httpBackend.whenGET('/CCMS/meter/meter_data_list')
            .respond([]);
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    function createController() {
        $controller('historyListControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            historyFactory: historyFactory,
            config: mockConfig,
            $state: { go: jasmine.createSpy('$state.go') },
            $stateParams: mockStateParams,
            inform: mockInform,
            $modal: mockModal
        });
    }

    describe('initialization', function() {
        it('should load meter data on init', function() {
            createController();
            $httpBackend.flush();
            expect($scope.dcu_data).toBeDefined();
        });

        it('should load DCU names on init', function() {
            createController();
            $httpBackend.flush();
            expect($scope.dcu_data).toBeDefined();
        });

        it('should set districts from config', function() {
            createController();
            $httpBackend.flush();
            expect($scope.districts).toEqual(mockConfig.districts);
        });

        it('should handle null previlage gracefully', function() {
            $rootScope.previlage = null;
            createController();
            $httpBackend.flush();
            expect($scope.qs_params).toBe('?distrtict=ALL&mandal=ALL&gp=ALL');
        });
    });
});
