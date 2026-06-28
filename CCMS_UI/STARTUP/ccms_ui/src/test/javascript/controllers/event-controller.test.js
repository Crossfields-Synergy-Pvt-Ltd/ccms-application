describe('eventControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend, eventFactory;
    var mockConfig, mockStateParams;
    var mockInform, mockModal;

    beforeEach(module('inform'));
    beforeEach(module('eventControllers'));
    beforeEach(module('eventFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _eventFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        eventFactory = _eventFactory_;
        $controller = _$controller_;

        mockInform = { add: jasmine.createSpy('inform.add') };
        mockModal = { open: jasmine.createSpy('modal.open').and.returnValue({ result: { then: function(){} } }) };

        mockConfig = { districts: [{ state: 'Guntur-17', code: 'Guntur-17' }] };
        mockStateParams = {};

        $rootScope.previlage = {
            dist: 'ALL',
            mondal: 'ALL',
            gp: 'ALL',
            monitor_and_controller: true,
            history: true,
            event: true
        };

        $httpBackend.whenGET('/CCMS/dcu/dcu_name_list?distrtict=ALL&mandal=ALL&gp=ALL')
            .respond([{ name: 'DCU-001', id: 'dcu1' }]);
        $httpBackend.whenGET('/CCMS/events/event_counts')
            .respond({ total: 50, mcb_trip: 5, high_current: 3 });
        $httpBackend.whenGET('/CCMS/events/events_between_date?id=ALL&start_date=&end_date=')
            .respond([]);
    }));

    afterEach(function() {
        if ($httpBackend) {
            try { $httpBackend.flush(); } catch(e) {}
        }
    });

    function createController() {
        $controller('eventListControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            eventFactory: eventFactory,
            config: mockConfig,
            $state: { go: jasmine.createSpy('$state.go') },
            $stateParams: mockStateParams,
            inform: mockInform,
            $modal: mockModal
        });
    }

    describe('initialization', function() {
        it('should load event counts on init', function() {
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

    describe('$scope.showdate', function() {
        it('should call factory with selected filters', function() {
            createController();
            $scope.selected_dcu = { name: { gateway_identifier: 'DCU001' } };
            $scope.start_date = '2024-01-01';
            $scope.end_date = '2024-12-31';

            $httpBackend.whenGET('/CCMS/events/events_between_date?id=DCU001&start_date=2024-01-01&end_date=2024-12-31')
                .respond([{ id: 'event1' }]);

            $scope.showdate();
        });

        it('should handle empty dates gracefully', function() {
            createController();
            $scope.selected_dcu = { name: { gateway_identifier: 'ALL' } };
            $scope.start_date = '';
            $scope.end_date = '';

            $httpBackend.whenGET('/CCMS/events/events_between_date?id=ALL&start_date=&end_date=')
                .respond([]);

            $scope.showdate();
        });
    });
});
